package youyihj.zenutils.impl.command;

import com.google.common.base.Suppliers;
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
import net.minecraft.util.text.TextFormatting;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getClickableCommandText;
import static crafttweaker.mc1120.commands.SpecialMessagesChat.getNormalMessage;

public class ReloadCommand extends CraftTweakerCommand {
    private static final String SCRIPT_LOADER_NAME = "reloadable";
    public static final Supplier<ScriptLoader> RELOADABLE_LOADER = Suppliers.memoize(() -> {
        ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader(SCRIPT_LOADER_NAME);
        loader.setMainName(SCRIPT_LOADER_NAME);
        // compatible to old scripts
        loader.addAliases("reloadableevents");
        return loader;
    });

    public ReloadCommand() {
        super("reload");
    }

    @Override
    protected void init() {
        setDescription(
                getClickableCommandText(TextFormatting.DARK_GREEN + "/ct reload", "/ct reload", true),
                getNormalMessage(TextFormatting.DARK_AQUA + "Reload reloadable scripts")
        );
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        if (server.isDedicatedServer()) {
            sender.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "The command only can be run in integrated server (SinglePlayer)!"));
            return;
        }
        sender.sendMessage(getNormalMessage(TextFormatting.AQUA + "Beginning reload scripts"));
        sender.sendMessage(getNormalMessage("Only scripts that marked " + TextFormatting.GRAY + "#loader reloadable " + TextFormatting.RESET + "can be reloaded."));
        sender.sendMessage(getNormalMessage(TextFormatting.YELLOW + "Recipe modifications are not reloadable, they will be ignored."));
        InternalUtils.getAllEventLists().forEach(EventList::clear);
        reRegisterInternalEvents();
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        ZenUtils.crafttweakerLogger.clear();

        // remove duplicate recipe name warning, since we don't register new recipes
        ZenUtilsGlobal.addRegexLogFilter("Recipe name \\[.*\\] has duplicate uses, defaulting to calculated hash!");

        ScriptLoader loader = RELOADABLE_LOADER.get();
        loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        if (InternalUtils.isContentTweakerInstalled()) {
            LateGetContentLookup.refreshFields();
            LateGetContentLookup.clear();
        }
        if (loader.getLoaderStage() == ScriptLoader.LoaderStage.ERROR) {
            sender.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "Failed to reload scripts"));
        } else {
            sender.sendMessage(getNormalMessage("Reloaded successfully"));
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
