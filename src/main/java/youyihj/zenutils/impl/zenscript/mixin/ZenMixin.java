package youyihj.zenutils.impl.zenscript.mixin;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.PreprocessorManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinProcessor;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.transformer.Proxy;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;
import youyihj.zenutils.impl.mixin.custom.CustomMixinPlugin;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author youyihj
 */
public class ZenMixin {
    private static final Logger LOGGER = LogManager.getLogger("ZenUtils Custom Mixin");

    public static void load() throws Exception {
        PreprocessorManager preprocessorManager = CraftTweakerAPI.tweaker.getPreprocessorManager();
        preprocessorManager.registerPreprocessorAction(MixinPreprocessor.NAME, (IMultilinePreprocessorFactory<MixinPreprocessor>) MixinPreprocessor::new);
        Class<?> classLoaderClass = Reference.IS_CLEANROOM ? Class.forName("top.outlands.foundation.boot.ActualClassLoader") : LaunchClassLoader.class;
        Field lclBytecodesField = classLoaderClass.getDeclaredField("resourceCache");
        lclBytecodesField.setAccessible(true);
        //noinspection unchecked
        Map<String, byte[]> resourceCache = (Map<String, byte[]>) lclBytecodesField.get(Launch.classLoader);
        CraftTweakerAPI.tweaker.setNetworkSide(FMLCommonHandler.instance().getSide().isClient() ? NetworkSide.SIDE_CLIENT : NetworkSide.SIDE_SERVER);
        CraftTweakerAPI.tweaker.loadScript(false, MixinPreprocessor.NAME);
        ZenModule.classes.forEach((name, bytecode) -> {
            if (name.startsWith("youyihj/zenutils/impl/mixin/custom/")) {
                resourceCache.put(name.replace('/', '.'), bytecode);
                String classSimpleName = name.substring("youyihj/zenutils/impl/mixin/custom/".length());
                CustomMixinPlugin.addMixinClass(classSimpleName);
                LOGGER.info("Loaded {}", classSimpleName);
            }
        });
        Mixins.addConfiguration("mixins.zenutils.custom.json");

        Field processorField = MixinTransformer.class.getDeclaredField("processor");
        processorField.setAccessible(true);
        IMixinProcessor processor = (IMixinProcessor) processorField.get(Proxy.transformer);
        Method selectMethod = processor.getClass().getDeclaredMethod("select", MixinEnvironment.class);
        selectMethod.setAccessible(true);
        selectMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());
    }
}
