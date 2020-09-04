package youyihj.zenutils.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface IZenCommand extends ICommand {
    @ZenMethod
    default void register() {
        ZenCommandRegistrar.zenCommandMap.put(this.getName(), this);
    }
}
