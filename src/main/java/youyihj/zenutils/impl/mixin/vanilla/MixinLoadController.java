package youyihj.zenutils.impl.mixin.vanilla;

import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.zenscript.entrypoint.CustomScriptEntrypoint;

/**
 * @author youyihj
 */
@Mixin(value = LoadController.class, remap = false)
public abstract class MixinLoadController {
    @Inject(method = "sendEventToModContainer", at = @At(value = "INVOKE", target = "Lcom/google/common/eventbus/EventBus;post(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void injectScriptEntrypoint(FMLEvent stateEvent, ModContainer mc, CallbackInfo ci) {
        if (stateEvent instanceof FMLConstructionEvent && mc instanceof FMLModContainer) {
            CustomScriptEntrypoint.injectToMod(((FMLModContainer) mc));
        }
    }
}
