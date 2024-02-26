package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.logger.MTLogger;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.logger.MCLogger;
import crafttweaker.mc1120.player.expand.ExpandPlayer;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.ILogger;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.api.command.ZenCommandRegisterAction;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.ftbq.FTBQEventManager;
import youyihj.zenutils.api.preprocessor.*;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.api.zenscript.SidedZenRegister;
import youyihj.zenutils.impl.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.impl.command.CleanLogCommand;
import youyihj.zenutils.impl.command.StatCommand;
import youyihj.zenutils.impl.player.IStatFormatterAdapter;
import youyihj.zenutils.impl.player.PlayerInteractionSimulation;
import youyihj.zenutils.impl.reload.ReloadCommand;
import youyihj.zenutils.impl.runtime.ScriptStatus;
import youyihj.zenutils.impl.runtime.ZenUtilsFileLogger;
import youyihj.zenutils.impl.runtime.ZenUtilsLogger;
import youyihj.zenutils.impl.runtime.ZenUtilsTweaker;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
@Mod.EventBusSubscriber
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.17.0";
    public static final String DEPENDENCIES = "required-after:crafttweaker;required-after:mixinbooter;before:contenttweaker;after:ftbquests;";
    public static final String MOD_COT = "contenttweaker";

    public static Logger forgeLogger;
    public static ZenUtilsLogger crafttweakerLogger;
    public static ZenUtilsTweaker tweaker;
    public static ASMDataTable asmDataTable;

    @SidedProxy(clientSide = "youyihj.zenutils.impl.player.IStatFormatterAdapter$Client", serverSide = "youyihj.zenutils.impl.player.IStatFormatterAdapter$Server")
    public static IStatFormatterAdapter statFormatterAdapter;

    @Mod.EventHandler
    public static void onConstruct(FMLConstructionEvent event) {
        InternalUtils.checkCraftTweakerVersion("4.1.20.692", () -> InternalUtils.hasMethod(ExpandPlayer.class, "isSpectator", IPlayer.class));
        registerGlobalMethods();
        PreprocessorManager preprocessorManager = CraftTweakerAPI.tweaker.getPreprocessorManager();
        preprocessorManager.registerPreprocessorAction(SuppressErrorPreprocessor.NAME, SuppressErrorPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(NoFixRecipeBookPreprocessor.NAME, NoFixRecipeBookPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(HardFailPreprocessor.NAME, HardFailPreprocessor::new);
        preprocessorManager.registerPreprocessorAction(ReloadablePreprocessor.NAME, ReloadablePreprocessor::new);
        preprocessorManager.registerPreprocessorAction(NotReloadablePreprocessor.NAME, NotReloadablePreprocessor::new);
        try {
            redirectLogger();
        } catch (Exception e) {
            CraftTweakerAPI.logWarning("Failed to set crafttweaker logger");
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
        CraftTweakerAPI.logInfo("Hey! Here is ZenUtils.");
        ZenWorldCapabilityHandler.register();
        PlayerInteractionSimulation.registerNetworkMessage();
        asmDataTable = event.getAsmData();
        forgeLogger = event.getModLog();
        readSidedZenRegisters(event.getSide());
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
        CTChatCommand.registerCommand(new CleanLogCommand());
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
        InternalUtils.setScriptStatus(ScriptStatus.STARTED);
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

    private static void redirectLogger() throws Exception {
        crafttweakerLogger = new ZenUtilsLogger();
        final Field loggerField = ReflectUtils.removePrivateFinal(CrafttweakerImplementationAPI.class, "logger");
        final Field mtLoggerSubLoggerField = ReflectUtils.removePrivate(MTLogger.class, "loggers");
        final Field mcLoggerWriterField = ReflectUtils.removePrivate(MCLogger.class, "printWriter");
        @SuppressWarnings("unchecked")
        List<ILogger> loggerList = (List<ILogger>) mtLoggerSubLoggerField.get(CrafttweakerImplementationAPI.logger);
        PrintWriter printWriter = (PrintWriter) mcLoggerWriterField.get(loggerList.get(0));
        printWriter.close();
        crafttweakerLogger.addLogger(new ZenUtilsFileLogger(FileSystems.getDefault().getPath("crafttweaker.log")));
        loggerField.set(null, crafttweakerLogger);
    }

    @SuppressWarnings("unchecked")
    private static void readSidedZenRegisters(Side side) {
        asmDataTable.getAll(SidedZenRegister.class.getCanonicalName()).forEach(data -> {
            Map<String, Object> annotationInfo = data.getAnnotationInfo();
            List<String> modDeps = (List<String>) annotationInfo.get("modDeps");
            List<ModAnnotation.EnumHolder> sides = (List<ModAnnotation.EnumHolder>) annotationInfo.get("value");
            boolean modSatisfied = modDeps == null || modDeps.stream().allMatch(Loader::isModLoaded);
            boolean sideSatisfied = sides == null || sides.stream().map(ModAnnotation.EnumHolder::getValue).anyMatch(side.name()::equals);
            if (modSatisfied && sideSatisfied) {
                try {
                    CraftTweakerAPI.registerClass(Class.forName(data.getClassName(), false, CraftTweaker.class.getClassLoader()));
                } catch (ClassNotFoundException e) {
                    CraftTweaker.LOG.catching(e);
                }
            }
        });
    }
}
