package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.command.IGetCommandUsage")
public interface IGetCommandUsage {
    IGetCommandUsage UNDEFINED = (sender -> "commands.undefined.usage");

    String getCommandUsage(ZenUtilsCommandSender sender);
}
