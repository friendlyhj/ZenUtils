package youyihj.zenutils.impl.reload;

import crafttweaker.mc1120.commands.CraftTweakerCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ScriptReloadEvent;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ScriptStatus;

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
        sender.sendMessage(getNormalMessage(TextFormatting.AQUA + "Beginning reload scripts"));
//        sender.sendMessage(getNormalMessage("Only scripts that marked " + TextFormatting.GRAY + "#reloadable " + TextFormatting.RESET + "can be reloaded."));
        if (!Loader.isModLoaded("zenrecipereloading")) {
            sender.sendMessage(getNormalMessage(TextFormatting.YELLOW + "Most recipe modifications are not reloadable, they will be ignored."));
        }
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        ZenUtils.crafttweakerLogger.clear();
        InternalUtils.setScriptStatus(ScriptStatus.RELOAD);
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Pre(sender));
        boolean successful = ScriptReloader.reloadScripts();
        if (successful) {
            sender.sendMessage(getNormalMessage("Reloaded successfully"));
        } else {
            sender.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "Failed to reload scripts"));
        }
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Post(sender));
        InternalUtils.setScriptStatus(ScriptStatus.STARTED);
    }
}
