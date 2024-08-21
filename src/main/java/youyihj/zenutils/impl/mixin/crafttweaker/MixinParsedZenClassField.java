package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.FieldVisitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassField;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.mixin.itf.IParsedZenClassFieldExtension;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.nat.MixinAnnotationTranslator;

/**
 * @author youyihj
 */
@Mixin(value = ParsedZenClassField.class, remap = false)
public abstract class MixinParsedZenClassField implements IParsedZenClassFieldExtension {
    private ZenPosition position;

    @WrapOperation(method = "parse", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/parser/Token;getValue()Ljava/lang/String;"))
    private static String recordPosition$0(Token instance, Operation<String> original, @Share("position") LocalRef<ZenPosition> position) {
        position.set(instance.getPosition());
        return original.call(instance);
    }

    @ModifyReturnValue(method = "parse", at = @At("RETURN"))
    private static ParsedZenClassField recordPosition$1(ParsedZenClassField original, @Share("position") LocalRef<ZenPosition> position) {
        ((IParsedZenClassFieldExtension) original).setPosition(position.get());
        return original;
    }

    @WrapOperation(method = "visit", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/FieldVisitor;visitEnd()V"))
    private void applyAnnotation(FieldVisitor instance, Operation<Void> original) {
        for (MixinPreprocessor mixinPreprocessor : MixinAnnotationTranslator.findAnnotation(position)) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(),
                    it -> instance.visitAnnotation(it, true),
                    it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
            );
        }
    }

    @Override
    public void setPosition(ZenPosition position) {
        this.position = position;
    }
}
