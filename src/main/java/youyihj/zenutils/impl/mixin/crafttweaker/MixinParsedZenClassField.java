package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassField;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.mixin.itf.IParsedZenClassFieldExtension;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.mixin.MixinAnnotationTranslator;

/**
 * @author youyihj
 */
@Mixin(value = ParsedZenClassField.class, remap = false)
public abstract class MixinParsedZenClassField implements IParsedZenClassFieldExtension {
    @Shadow
    @Final
    private String ownerName;
    private ZenPosition position;
    private boolean isSynthetic;

    @WrapOperation(method = "parse", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/parser/Token;getValue()Ljava/lang/String;"))
    private static String recordPosition$0(Token instance, Operation<String> original, @Share("position") LocalRef<ZenPosition> position) {
        position.set(instance.getPosition());
        return original.call(instance);
    }

    @ModifyReturnValue(method = "parse", at = @At("RETURN"))
    private static ParsedZenClassField recordPosition$1(ParsedZenClassField original, @Share("position") LocalRef<ZenPosition> position) {
        IParsedZenClassFieldExtension fieldExtension = (IParsedZenClassFieldExtension) original;
        fieldExtension.setPosition(position.get());

        if (fieldExtension.getOwnerName().startsWith("youyihj/zenutils/impl/mixin/custom/")) {
            for (MixinPreprocessor mixinPreprocessor : MixinAnnotationTranslator.findAnnotation(position.get())) {
                String type = mixinPreprocessor.getAnnotation().getLeft();
                if (type.equals("Shadow")) {
                    return original;
                }
            }

            if (original.isStatic) {
                ((IParsedZenClassFieldExtension) original).setSynthetic(true);
            }
        }

        return original;
    }

    @ModifyConstant(method = "visit", constant = @Constant(intValue = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC))
    private int modifyModifiers(int constant) {
        if (isSynthetic) {
            return constant | Opcodes.ACC_SYNTHETIC;
        }
        return constant;
    }

    @WrapOperation(method = "visit", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/FieldVisitor;visitEnd()V"))
    private void applyAnnotation(FieldVisitor instance, Operation<Void> original) {
        if (position == null) {
            return;
        }
        for (MixinPreprocessor mixinPreprocessor : MixinAnnotationTranslator.findAnnotation(position)) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(), position,
                    instance::visitAnnotation
            );
        }
        if (isSynthetic) {
            instance.visitAnnotation("Lyouyihj/zenutils/impl/member/VisibleSynthetic;", true);
        }
    }

    @Override
    public void setPosition(ZenPosition position) {
        this.position = position;
    }

    @Override
    public void setSynthetic(boolean synthetic) {
        isSynthetic = synthetic;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }
}
