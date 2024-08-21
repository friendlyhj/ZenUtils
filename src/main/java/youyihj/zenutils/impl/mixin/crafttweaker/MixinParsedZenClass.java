package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.nat.MixinAnnotationTranslator;

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
    public String className;

    @Shadow
    public Class<?> thisClass;

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/ClassWriter;visitEnd()V"))
    private void applyAnnotation(IEnvironmentGlobal environmentGlobal, CallbackInfo ci, @Local ClassWriter cw) {
        for (MixinPreprocessor mixinPreprocessor : MixinAnnotationTranslator.findAnnotation(position)) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(),
                    cw::visitAnnotation,
                    it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
            );
        }
    }

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/IEnvironmentGlobal;putClass(Ljava/lang/String;[B)V", shift = At.Shift.AFTER), cancellable = true)
    private void denyLoadMixinClass(IEnvironmentGlobal environmentGlobal, CallbackInfo ci) {
        if (className.startsWith("youyihj/zenutils/impl/mixin")) {
            thisClass = Object.class;
            ci.cancel();
        }
    }
}
