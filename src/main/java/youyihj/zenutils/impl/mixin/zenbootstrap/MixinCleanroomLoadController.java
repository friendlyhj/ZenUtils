package youyihj.zenutils.impl.mixin.zenbootstrap;

import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;

/**
 * @author youyihj
 */
@Mixin(value = LoadController.class, remap = false)
public class MixinCleanroomLoadController {
    @Inject(
            method = "distributeStateMessage(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V",
            at = @At(value = "INVOKE", target = "Lcom/google/common/eventbus/EventBus;post(Ljava/lang/Object;)V")
    )
    private void beforeConstructing(LoaderState state, Object[] eventData, CallbackInfo ci) throws Throwable {
        if (state == LoaderState.CONSTRUCTING) {
            ZenMixin.load();
        }
    }
}