package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;

/**
 * @author youyihj
 */
@Mixin(targets = "stanhebben.zenscript.ZenModule$MyClassLoader", remap = false)
public abstract class MixinZenClassLoader extends ClassLoader {
    @Inject(method = "loadClass(Ljava/lang/String;)Ljava/lang/Class;", at = @At("HEAD"), remap = false, cancellable = true)
    private void delegateToParentForInjectedMixinClassLoad(String name, CallbackInfoReturnable<Class<?>> cir) throws ClassNotFoundException {
        if (ZenMixin.isNonMixinClassInjected(name)) {
            cir.setReturnValue(super.loadClass(name));
        }
    }

    @Inject(method = "findClass(Ljava/lang/String;)Ljava/lang/Class;", at = @At("HEAD"), remap = false, cancellable = true)
    private void delegateToParentForInjectedMixinClassFind(String name, CallbackInfoReturnable<Class<?>> cir) throws ClassNotFoundException {
        if (ZenMixin.isNonMixinClassInjected(name)) {
            cir.setReturnValue(super.findClass(name));
        }
    }
}
