package youyihj.zenutils.impl.zenscript.mixin;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.MixinProcessor;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.transformer.Proxy;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;
import youyihj.zenutils.impl.mixin.custom.CustomMixinPlugin;
import youyihj.zenutils.impl.runtime.ScriptStatus;
import youyihj.zenutils.impl.runtime.ZenUtilsTweaker;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author youyihj
 */
public class ZenMixin {
    private static final Logger LOGGER = LogManager.getLogger("ZenMixin");

    private static final Multiset<String> mixinNameUsedCounter = HashMultiset.create();

    public static void load() throws Exception {
        ITweaker tweaker = CraftTweakerAPI.tweaker;
        Preconditions.checkState(tweaker instanceof ZenUtilsTweaker, "CraftTweaker ITweaker is not redirected. A mixin config is failed. please report to the mod author!");
        PreprocessorManager preprocessorManager = tweaker.getPreprocessorManager();
        preprocessorManager.registerPreprocessorAction(MixinPreprocessor.NAME, (IMultilinePreprocessorFactory<MixinPreprocessor>) MixinPreprocessor::new);
        Class<?> classLoaderClass = Reference.IS_CLEANROOM ? Class.forName("top.outlands.foundation.boot.ActualClassLoader") : LaunchClassLoader.class;
        Field lclBytecodesField = classLoaderClass.getDeclaredField("resourceCache");
        lclBytecodesField.setAccessible(true);
        //noinspection unchecked
        Map<String, byte[]> resourceCache = (Map<String, byte[]>) lclBytecodesField.get(Launch.classLoader);
        tweaker.setNetworkSide(FMLCommonHandler.instance().getSide().isClient() ? NetworkSide.SIDE_CLIENT : NetworkSide.SIDE_SERVER);
        tweaker.loadScript(false, MixinPreprocessor.NAME);
        ZenModule.classes.forEach((name, bytecode) -> {
            if (name.startsWith("youyihj/zenutils/impl/mixin/custom/")) {
                resourceCache.put(name.replace('/', '.'), bytecode);
                String classSimpleName = name.substring("youyihj/zenutils/impl/mixin/custom/".length());
                CustomMixinPlugin.addMixinClass(classSimpleName);
                LOGGER.info("Loaded mixin class {}", classSimpleName);
                CraftTweakerAPI.logInfo("Loaded mixin class: " + classSimpleName);
            } else {
                LOGGER.info("Injecting non-mixin class: {} to mc LaunchClassLoader", name);
                CraftTweakerAPI.logInfo("Adding non-mixin class: " + name + " to mc LaunchClassLoader");
                resourceCache.put(name, bytecode);
            }
        });
        Mixins.addConfiguration("mixins.zenutils.custom.json");
        Mixins.registerErrorHandlerClass("youyihj.zenutils.impl.mixin.custom.CustomMixinErrorHandler");

        Field processorField = MixinTransformer.class.getDeclaredField("processor");
        processorField.setAccessible(true);
        MixinProcessor processor = (MixinProcessor) processorField.get(Proxy.transformer);
        Method selectMethod = processor.getClass().getDeclaredMethod("select", MixinEnvironment.class);
        selectMethod.setAccessible(true);
        selectMethod.invoke(processor, Reference.IS_CLEANROOM ? MixinEnvironment.getDefaultEnvironment() : MixinEnvironment.getCurrentEnvironment());
    }

    public static String handleMixinClassName(String className) {
        String mixinClassName = "youyihj/zenutils/impl/mixin/custom/" + className;
        if (InternalUtils.getScriptStatus() != ScriptStatus.SYNTAX) {
            int usedCountBefore = mixinNameUsedCounter.count(className);
            mixinNameUsedCounter.add(className);
            if (usedCountBefore != 0) {
                mixinClassName += usedCountBefore;
            }
        }
        return mixinClassName;
    }
}
