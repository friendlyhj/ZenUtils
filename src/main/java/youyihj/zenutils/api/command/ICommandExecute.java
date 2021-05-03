package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import net.minecraft.command.CommandException;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.ICommandExecute")
public interface ICommandExecute {
    void execute(ZenCommand command, IServer server, ZenUtilsCommandSender sender, String[] args) throws CommandException;
}
