package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.runtime.CrTTweaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author youyihj
 */
@Mixin(value = CrTTweaker.class, remap = false)
public abstract class MixinCrTTweaker {
    @ModifyArg(method = "loadScript(ZLcrafttweaker/runtime/ScriptLoader;Ljava/util/List;Z)Z", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/ClassNameGenerator;setPrefix(Ljava/lang/String;)V"))
    private String setMixinClassPackage(String prefix) {
        if ("mixin".equalsIgnoreCase(prefix)) {
            return "youyihj/zenutils/impl/mixin/custom/";
        }
        return prefix;
    }
}
