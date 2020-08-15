package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.server.MCServer;
import net.minecraft.command.CommandException;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

@ZenRegister
@ZenClass("mods.zenutils.command.ICommandExecute")
public interface ICommandExecute {
    void execute(ZenCommand command,MCServer server, ZenUtilsCommandSender sender, String[] args) throws CommandException;
}
