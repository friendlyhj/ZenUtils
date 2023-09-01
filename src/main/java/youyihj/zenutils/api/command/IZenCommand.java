package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.command.ICommand;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.IZenCommand")
public interface IZenCommand extends ICommand {
    @ZenMethod
    default void register() {
        ZenCommandRegisterAction.ApplyLogic.INSTANCE.apply(new ZenCommandRegisterAction(this));
    }
}
