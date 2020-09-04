package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.command.IGetCommandUsage")
public interface IGetCommandUsage {
    IGetCommandUsage UNDEFINED = (sender -> "commands.undefined.usage");

    String getCommandUsage(ZenUtilsCommandSender sender);
}
