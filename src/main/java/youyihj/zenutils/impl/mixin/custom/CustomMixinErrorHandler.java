package youyihj.zenutils.impl.mixin.custom;

import crafttweaker.CraftTweakerAPI;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class CustomMixinErrorHandler implements IMixinErrorHandler {
    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if ("mixins.zenutils.custom.json".equals(mixin.getConfig().getName())) {
            CraftTweakerAPI.logError("Error occurred when applying mixin " + mixin.getName() + " to class " + targetClassName, th);
        }
        return action;
    }
}
