package youyihj.zenutils.impl.command;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.event.MTEventManager;
import crafttweaker.api.event.PlayerLoggedInEvent;
import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.runtime.ScriptLoader;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.*;

public class ReloadEventCommand extends CraftTweakerCommand {
    private static final String SCRIPT_LOADER_NAME = "reloadableevents";

    public ReloadEventCommand() {
        super("reloadevents");
    }

    @Override
    protected void init() {
        setDescription(getClickableCommandText("\u00A72/ct reloadevents", "/ct reloadevents", true), getNormalMessage(" \u00A73Reload event handlers"));
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        if (server.isDedicatedServer()) {
            sender.sendMessage(getNormalMessage("\u00A7cThe command only can be run in integrated server (SinglePlayer)!"));
            return;
        }
        sender.sendMessage(getNormalMessage("\u00A7bBeginning reload for events"));
        InternalUtils.getAllEventLists().forEach(EventList::clear);
        reRegisterInternalEvents();
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        ZenUtils.crafttweakerLogger.clear();

        // remove duplicate recipe name warning, since we don't register new recipes
        ZenUtilsGlobal.addRegexLogFilter("Recipe name \\[.*\\] has duplicate uses, defaulting to calculated hash!");

        final ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader(SCRIPT_LOADER_NAME);
        loader.setMainName(SCRIPT_LOADER_NAME);
        loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        if (InternalUtils.isContentTweakerInstalled()) {
            LateGetContentLookup.refreshFields();
            LateGetContentLookup.clear();
        }
        if (loader.getLoaderStage() == ScriptLoader.LoaderStage.ERROR) {
            sender.sendMessage(getNormalMessage("\u00a74 Failed to reload for events"));
        } else {
            sender.sendMessage(getNormalMessage("Reload for events successfully"));
        }
    }

    @SuppressWarnings("unchecked")
    private void reRegisterInternalEvents() {
        try {
            Field listenLogin = ReflectUtils.removePrivate(CrafttweakerImplementationAPI.class, "LISTEN_LOGIN");
            Field listenLogout = ReflectUtils.removePrivate(CrafttweakerImplementationAPI.class, "LISTEN_LOGOUT");
            MTEventManager events = CrafttweakerImplementationAPI.events;
            events.onPlayerLoggedIn((IEventHandler<PlayerLoggedInEvent>) listenLogin.get(null));
            events.onPlayerLoggedOut((IEventHandler<PlayerLoggedOutEvent>) listenLogout.get(null));
            events.onPlayerInteract(CrafttweakerImplementationAPI.LISTEN_BLOCK_INFO);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
