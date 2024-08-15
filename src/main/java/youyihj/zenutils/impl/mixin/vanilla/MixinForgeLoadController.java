package youyihj.zenutils.impl.mixin.vanilla;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.PreprocessorManager;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

/**
 * @author youyihj
 */
@Mixin(value = LoadController.class, remap = false, priority = Integer.MAX_VALUE)
public class MixinForgeLoadController {
    @Inject(method = "distributeStateMessage(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V", at = @At("HEAD"))
    private void beforeConstructing(LoaderState state, Object[] eventData, CallbackInfo ci) throws Throwable {
        if (state == LoaderState.CONSTRUCTING) {
            PreprocessorManager preprocessorManager = CraftTweakerAPI.tweaker.getPreprocessorManager();
            preprocessorManager.registerPreprocessorAction(MixinPreprocessor.NAME, (IMultilinePreprocessorFactory<MixinPreprocessor>) MixinPreprocessor::new);
            CraftTweakerAPI.tweaker.loadScript(false, MixinPreprocessor.NAME);
            for (String className : ZenModule.classes.keySet()) {
                CraftTweakerAPI.logInfo(className);
            }
        }
    }
}
