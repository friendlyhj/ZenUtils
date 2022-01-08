package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.command.ICommandManager;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.delegate.NoFeedbackCommandSender;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.command.ICommandManager")
public class ExpandedCommandManager {
    @ZenMethod
    public static int executeCommandSilent(ICommandManager commandManager, ICommandSender sender, String rawCommand) {
        return CraftTweaker.server.commandManager.executeCommand(new NoFeedbackCommandSender(CraftTweakerMC.getICommandSender(sender)), rawCommand);
    }
}
