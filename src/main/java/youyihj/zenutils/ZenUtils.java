package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.player.expand.ExpandPlayer;
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
import youyihj.zenutils.api.zenscript.SidedZenRegister;
import youyihj.zenutils.impl.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.impl.command.CleanLogCommand;
import youyihj.zenutils.impl.command.StatCommand;
import youyihj.zenutils.impl.network.PlayerScriptValidation;
import youyihj.zenutils.impl.network.ZenUtilsNetworkHandler;
import youyihj.zenutils.impl.player.IStatFormatterAdapter;
import youyihj.zenutils.impl.player.PlayerInteractionSimulation;
import youyihj.zenutils.impl.reload.ReloadCommand;
import youyihj.zenutils.impl.runtime.ScriptStatus;
import youyihj.zenutils.impl.runtime.ZenUtilsLogger;
import youyihj.zenutils.impl.runtime.ZenUtilsTweaker;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
@Mod.EventBusSubscriber
public class ZenUtils {
    public static Logger forgeLogger;
    public static ZenUtilsLogger crafttweakerLogger;
    public static ZenUtilsTweaker tweaker;
    public static ASMDataTable asmDataTable;

    @SidedProxy(clientSide = "youyihj.zenutils.impl.player.IStatFormatterAdapter$Client", serverSide = "youyihj.zenutils.impl.player.IStatFormatterAdapter$Server")
    public static IStatFormatterAdapter statFormatterAdapter;

    @Mod.EventHandler
    public static void onConstruct(FMLConstructionEvent event) {
        InternalUtils.checkCraftTweakerVersion("4.1.20.692", () -> InternalUtils.hasMethod(ExpandPlayer.class, "isSpectator", IPlayer.class));
        try {
            crafttweakerLogger = (ZenUtilsLogger) CraftTweakerAPI.getLogger();
            tweaker = (ZenUtilsTweaker) CraftTweakerAPI.tweaker;
        } catch (ClassCastException e) {
            throw new IllegalStateException("CraftTweaker ITweaker or Logger is not redirected. A mixin config is failed. please report to the mod author!");
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
        if (ZenUtilsNetworkHandler.INSTANCE.shouldValidateScripts(event.getServer())) {
            MinecraftForge.EVENT_BUS.register(PlayerScriptValidation.ServerEventHandler.class);
        }
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
