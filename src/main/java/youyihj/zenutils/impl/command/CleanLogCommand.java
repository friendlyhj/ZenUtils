package youyihj.zenutils.impl.command;

import crafttweaker.mc1120.commands.CraftTweakerCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import youyihj.zenutils.ZenUtils;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getClickableCommandText;
import static crafttweaker.mc1120.commands.SpecialMessagesChat.getNormalMessage;

/**
 * @author youyihj
 */
public class CleanLogCommand extends CraftTweakerCommand {
    public CleanLogCommand() {
        super("clean");
    }

    @Override
    protected void init() {
        setDescription(
                getClickableCommandText(TextFormatting.DARK_GREEN + "/ct clean", "/ct clean", true),
                getNormalMessage(TextFormatting.DARK_AQUA + "Cleans crafttweaker log.")
        );
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        ZenUtils.crafttweakerLogger.clean();
        sender.sendMessage(new TextComponentString("cleaned the log."));
    }
}
