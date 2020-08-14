package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

@ZenRegister
@ZenClass("mods.zenutils.command.IGetCommandUsage")
public interface IGetCommandUsage {
    String getCommandUsage(ZenUtilsCommandSender sender);
}
