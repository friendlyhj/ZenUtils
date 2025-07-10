package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.util.EventList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
@Mixin(value = EventList.class, remap = false)
public abstract class MixinEventList {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerThis(CallbackInfo ci) {
        InternalUtils.registerEventList((EventList<?>) (Object) this);
    }
}
