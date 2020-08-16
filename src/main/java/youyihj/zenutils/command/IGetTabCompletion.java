package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import javax.annotation.Nullable;
import java.util.Collection;

@ZenRegister
@ZenClass("mods.zenutils.IGetTabCompletion")
public interface IGetTabCompletion {
    Collection<?> get(IServer server, ZenUtilsCommandSender sender, String[] args, @Nullable IBlockPos targetPos);
}
