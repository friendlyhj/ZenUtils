package youyihj.zenutils.impl.mixin.vanilla;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.PreprocessorManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinProcessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.Proxy;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;
import youyihj.zenutils.impl.mixin.custom.CustomMixinPlugin;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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
            Field lclBytecodesField = LaunchClassLoader.class.getDeclaredField("resourceCache");
            lclBytecodesField.setAccessible(true);
            //noinspection unchecked
            Map<String, byte[]> resourceCache = (Map<String, byte[]>) lclBytecodesField.get(Launch.classLoader);
            CraftTweakerAPI.tweaker.loadScript(false, MixinPreprocessor.NAME);
            ZenModule.classes.forEach((name, bytecode) -> {
                if (name.startsWith("youyihj/zenutils/impl/mixin/custom/")) {
                    resourceCache.put(name.replace('/', '.'), bytecode);
                    CustomMixinPlugin.addMixinClass(name.substring("youyihj/zenutils/impl/mixin/custom/".length()));
                }
            });
            Mixins.addConfiguration("mixins.zenutils.custom.json");

            IMixinProcessor processor = ((IMixinTransformer) Proxy.transformer).getProcessor();
            Method selectMethod = processor.getClass().getDeclaredMethod("select", MixinEnvironment.class);
            selectMethod.setAccessible(true);
            selectMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());
        }
    }
}
