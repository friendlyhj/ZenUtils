package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.mc1120.preprocessors.ModLoadedPreprocessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zone.rong.mixinbooter.service.ModDiscoverer;

/**
 * @author youyihj
 */
@Mixin(value = ModLoadedPreprocessor.class, remap = false)
public class MixinModLoadedPreprocessor {
    @Redirect(method = "checkAreLoaded", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/Loader;isModLoaded(Ljava/lang/String;)Z"))
    private boolean redirectEarlyModLoaded(String modid) {
        return ModDiscoverer.isModPresent(modid);
    }
}
