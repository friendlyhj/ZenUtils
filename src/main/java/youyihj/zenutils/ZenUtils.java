package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.command.ZenCommandRegistrar;
import youyihj.zenutils.command.internal.ReloadEventCommand;
import youyihj.zenutils.ftbq.FTBQEventManager;
import youyihj.zenutils.logger.ZenUtilsLogger;
import youyihj.zenutils.preprocessor.HardFailPreprocessor;
import youyihj.zenutils.preprocessor.NoFixRecipeBookPreprocessor;
import youyihj.zenutils.preprocessor.SuppressErrorPreprocessor;
import youyihj.zenutils.util.InternalUtils;
import youyihj.zenutils.util.ReflectUtils;
import youyihj.zenutils.util.ZenUtilsGlobal;
import youyihj.zenutils.util.ZenUtilsTweaker;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
@Mod.EventBusSubscriber
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.6.8";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker;required-after:redstoneflux;after:ftbquests";

    public static Logger forgeLogger;
    public static ZenUtilsLogger crafttweakerLogger;
    public static ZenUtilsTweaker tweaker;

    @Mod.EventHandler
    public static void onConstruct(FMLConstructionEvent event) {
        InternalUtils.checkCrTVersion();
        GlobalRegistry.registerGlobal("typeof", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "typeof", Object.class));
        GlobalRegistry.registerGlobal("toString", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "toString", Object.class));
        GlobalRegistry.registerGlobal("addRegexLogFilter", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "addRegexLogFilter", String.class));
        PreprocessorManager preprocessorManager = CraftTweakerAPI.tweaker.getPreprocessorManager();
        preprocessorManager.registerPreprocessorAction(SuppressErrorPreprocessor.NAME, SuppressErrorPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(NoFixRecipeBookPreprocessor.NAME, NoFixRecipeBookPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(HardFailPreprocessor.NAME, HardFailPreprocessor::new);
        try {
            crafttweakerLogger = new ZenUtilsLogger(CrafttweakerImplementationAPI.logger);
            final Field loggerField = ReflectUtils.removePrivateFinal(CrafttweakerImplementationAPI.class, "logger");
            loggerField.set(null, crafttweakerLogger);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            CraftTweakerAPI.logInfo("Fail to set crafttweaker logger to zenutils one. #suppress preprocessor cannot work properly.");
            e.printStackTrace();
        }
        try {
            tweaker = new ZenUtilsTweaker(CraftTweakerAPI.tweaker);
            final Field tweakerField = ReflectUtils.removePrivateFinal(CraftTweakerAPI.class, "tweaker");
            tweakerField.set(null, tweaker);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            CraftTweakerAPI.logWarning("Fail to set crafttweaker tweaker");
            e.printStackTrace();
        }
        if (Loader.isModLoaded("ftbquests")) {
            MinecraftForge.EVENT_BUS.register(FTBQEventManager.Handler.class);
        }
    }

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        ZenWorldCapabilityHandler.register();
        forgeLogger = event.getModLog();
        try {
            InternalUtils.scanAllEventLists();
        } catch (NoSuchFieldException e) {
            forgeLogger.error("failed to scan all event lists", e);
        }
    }

    @Mod.EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        CTChatCommand.registerCommand(new ReloadEventCommand());
        ZenCommandRegistrar.zenCommandMap.forEach((name, command) -> event.registerServerCommand(command));
    }
}
