package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.mixin.itf.IMixinTargetEnvironment;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.mixin.ExpressionMixinThis;
import youyihj.zenutils.impl.zenscript.mixin.MixinAnnotationTranslator;

import java.util.List;

/**
 * @author youyihj
 */
@Mixin(value = ParsedZenClass.class, remap = false)
public abstract class MixinParsedZenClass {
    @Shadow
    @Final
    public ZenPosition position;

    @Shadow
    @Final
    @Mutable
    public String className;

    @Shadow
    public Class<?> thisClass;

    @Shadow
    @Final
    private EnvironmentScript classEnvironment;

    @Unique
    List<MixinPreprocessor> preprocessors;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initAnnotation(ZenPosition position, String name, String className, EnvironmentScript classEnvironment, CallbackInfo ci) {
        this.preprocessors = MixinAnnotationTranslator.findAnnotation(position);
        for (MixinPreprocessor preprocessor : preprocessors) {
            Pair<String, JsonElement> annotation = preprocessor.getAnnotation();
            if (annotation.getLeft().equals("Mixin")) {
                this.className = "youyihj/zenutils/impl/mixin/custom/" + name;
                ((IMixinTargetEnvironment) classEnvironment).setTargets(MixinAnnotationTranslator.getMixinTargets(annotation.getRight().getAsJsonObject()));
                break;
            }
        }
    }

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/ClassWriter;visit(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void applyAnnotation(IEnvironmentGlobal environmentGlobal, CallbackInfo ci, @Local ClassWriter cw, @Share("target") LocalRef<String> targetRef) {
        for (MixinPreprocessor mixinPreprocessor : preprocessors) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(),
                    cw::visitAnnotation,
                    it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
            );
            if (annotation.getLeft().equals("Mixin")) {
                List<String> mixinTargets = ((IMixinTargetEnvironment) classEnvironment).getTargets();
                if (mixinTargets.size() == 1) {
                    targetRef.set(mixinTargets.get(0).replace('.', '/'));
                }
            }
        }
    }

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/IEnvironmentGlobal;putClass(Ljava/lang/String;[B)V", shift = At.Shift.AFTER), cancellable = true)
    private void denyLoadMixinClass(IEnvironmentGlobal environmentGlobal, CallbackInfo ci) {
        if (className.startsWith("youyihj/zenutils/impl/mixin")) {
            thisClass = Object.class;
            ci.cancel();
        }
    }

    @WrapOperation(method = "writeClass", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/EnvironmentClass;putValue(Ljava/lang/String;Lstanhebben/zenscript/symbols/IZenSymbol;Lstanhebben/zenscript/util/ZenPosition;)V"))
    private void injectThis0(EnvironmentClass instance, String name, IZenSymbol value, ZenPosition position, Operation<Void> original, @Share("target") LocalRef<String> targetRef) {
        original.call(instance, name, value, position);
        String target = targetRef.get();
        if (target != null) {
            instance.putValue("this0", position1 -> new ExpressionMixinThis(position1, target, instance), position);
        }
    }
}
