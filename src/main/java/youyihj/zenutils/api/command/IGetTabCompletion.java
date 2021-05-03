package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import net.minecraft.command.CommandException;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.util.StringList;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.command.IGetTabCompletion")
public interface IGetTabCompletion {
    StringList get(IServer server, ZenUtilsCommandSender sender, @Nullable IBlockPos targetPos) throws CommandException;
}
