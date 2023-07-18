package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraft.command.CommandHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.api.command.ZenCommandRegisterAction;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.ftbq.FTBQEventManager;
import youyihj.zenutils.api.preprocessor.HardFailPreprocessor;
import youyihj.zenutils.api.preprocessor.NoFixRecipeBookPreprocessor;
import youyihj.zenutils.api.preprocessor.ReloadablePreprocessor;
import youyihj.zenutils.api.preprocessor.SuppressErrorPreprocessor;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.impl.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.impl.command.StatCommand;
import youyihj.zenutils.impl.delegate.ZenUtilsLogger;
import youyihj.zenutils.impl.delegate.ZenUtilsTweaker;
import youyihj.zenutils.impl.reload.ReloadCommand;
import youyihj.zenutils.impl.util.IStatFormatterAdapter;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.PlayerInteractionSimulation;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
@Mod.EventBusSubscriber
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.14.4";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker;required-after:redstoneflux;after:ftbquests;required-after:mixinbooter";

    public static Logger forgeLogger;
    public static ZenUtilsLogger crafttweakerLogger;
    public static ZenUtilsTweaker tweaker;

    @SidedProxy(clientSide = "youyihj.zenutils.impl.util.IStatFormatterAdapter$Client", serverSide = "youyihj.zenutils.impl.util.IStatFormatterAdapter$Server")
    public static IStatFormatterAdapter statFormatterAdapter;

    @Mod.EventHandler
    public static void onConstruct(FMLConstructionEvent event) {
        InternalUtils.checkCraftTweakerVersion("4.1.20.673", () -> InternalUtils.hasMethod(CraftTweakerMC.class, "getIItemStackForMatching", ItemStack.class));
        registerGlobalMethods();
        PreprocessorManager preprocessorManager = CraftTweakerAPI.tweaker.getPreprocessorManager();
        preprocessorManager.registerPreprocessorAction(SuppressErrorPreprocessor.NAME, SuppressErrorPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(NoFixRecipeBookPreprocessor.NAME, NoFixRecipeBookPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(HardFailPreprocessor.NAME, HardFailPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(ReloadablePreprocessor.NAME, ReloadablePreprocessor::new);
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
        PlayerInteractionSimulation.registerNetworkMessage();
        forgeLogger = event.getModLog();
        try {
            InternalUtils.scanAllEventLists();
        } catch (NoSuchFieldException e) {
            forgeLogger.error("failed to scan all event lists", e);
        }
    }

    @Mod.EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        CTChatCommand.registerCommand(new ReloadCommand());
        CTChatCommand.registerCommand(new StatCommand());
        ZenCommandRegisterAction.ApplyLogic.INSTANCE.init((CommandHandler) event.getServer().commandManager);
        if (InternalUtils.isContentTweakerInstalled()) {
            LateGetContentLookup.refreshFields();
            LateGetContentLookup.clear();
        }
    }

    @Mod.EventHandler
    public static void onServerStop(FMLServerStoppedEvent event) {
        ZenCommandRegisterAction.ApplyLogic.INSTANCE.clean();
    }

    @Mod.EventHandler
    public static void onServerStarted(FMLServerStartedEvent event) {
        CraftTweakerAPI.tweaker.getActions().clear();
    }

    private static void registerGlobalMethods() {
        for (Method method : ZenUtilsGlobal.class.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            String name = method.getName();
            // skip typeof for primitive types
            if (name.equals("typeof") && parameterTypes[0].isPrimitive())
                continue;
            GlobalRegistry.registerGlobal(name, CraftTweakerAPI.getJavaStaticMethodSymbol(ZenUtilsGlobal.class, name, parameterTypes));
        }
    }
}
