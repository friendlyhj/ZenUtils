package youyihj.zenutils.impl.mixin.simpledimensions;

import lumien.simpledimensions.server.WorldCustom;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author youyihj
 */
@Mixin(WorldCustom.class)
public abstract class MixinWorldCustom extends World {
    protected MixinWorldCustom(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void zenutils$injectCapabilities(CallbackInfoReturnable<World> cir) {
        initCapabilities();
    }
}
