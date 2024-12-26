package youyihj.zenutils.impl.reload;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ScriptReloadEvent;
import youyihj.zenutils.impl.runtime.ScriptStatus;
import youyihj.zenutils.impl.util.InternalUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getClickableCommandText;
import static crafttweaker.mc1120.commands.SpecialMessagesChat.getNormalMessage;

public class ReloadCommand extends CraftTweakerCommand {

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
        if (args.length != 0 && "clean".equals(args[0])) {
            ZenUtils.crafttweakerLogger.clean();
        }
        reloadScripts(sender);
    }

    @Override
    public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.singletonList("clean");
    }

    /**
     * Partial reload stuff that can be easily accessed by other mods.
     * Not suggested, not sure if there are some issues reloading scripts on server.
     */
    public static void reloadScripts(ICommandSender requester) {
        requester.sendMessage(getNormalMessage(TextFormatting.AQUA + "Beginning reload scripts"));
//        requester.sendMessage(getNormalMessage("Only scripts that marked " + TextFormatting.GRAY + "#reloadable " + TextFormatting.RESET + "can be reloaded."));
        if (!Loader.isModLoaded("zenrecipereloading")) {
            requester.sendMessage(getNormalMessage(TextFormatting.YELLOW + "Most recipe modifications are not reloadable, they will be ignored."));
        }
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        ZenUtils.crafttweakerLogger.clear();
        InternalUtils.setScriptStatus(ScriptStatus.RELOAD);
        CraftTweakerAPI.logDefault("Request to reload script, executing pre-reload event...");
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Pre(requester));
        CraftTweakerAPI.logDefault("Reloading scripts...");
        boolean successful = ScriptReloader.reloadScripts();
        if (successful) {
            requester.sendMessage(getNormalMessage("Reloaded successfully"));
        } else {
            requester.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "Failed to reload scripts"));
        }
        CraftTweakerAPI.logDefault("Scripts reloaded, executing post-reload event...");
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Post(requester));
        CraftTweakerAPI.logDefault("Script reload finished.");
        InternalUtils.setScriptStatus(ScriptStatus.STARTED);
    }
}
