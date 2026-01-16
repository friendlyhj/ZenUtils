package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
@Mixin(value = CraftTweaker.class, remap = false)
public abstract class MixinCraftTweaker {
    @Inject(method = "onConstruction", at = @At("HEAD"))
    private void gainASMTable(FMLConstructionEvent event, CallbackInfo ci) {
        if (InternalUtils.asmDataTable == null) {
            InternalUtils.asmDataTable = event.getASMHarvestedData();
        }
    }
}
