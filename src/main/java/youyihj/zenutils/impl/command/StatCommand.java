package youyihj.zenutils.impl.command;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextFormatting;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.*;

/**
 * @author youyihj
 */
public class StatCommand extends CraftTweakerCommand {
    public StatCommand() {
        super("stats");
    }

    @Override
    protected void init() {
        setDescription(
                getClickableCommandText(TextFormatting.DARK_GREEN + "/ct stats", "/ct stats", true),
                getNormalMessage(TextFormatting.DARK_AQUA + "Outputs a list of all basic stats.")
        );
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        CraftTweakerAPI.logCommand("Basic Stats:");
        for (StatBase stat : StatList.BASIC_STATS) {
            CraftTweakerAPI.logCommand(stat.statId);
        }
        sender.sendMessage(getLinkToCraftTweakerLog("Basic stat list generated", sender));
    }
}
