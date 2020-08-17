package youyihj.zenutils.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface IZenCommand extends ICommand {
    IGetCommandUsage getCommandUsage = (sender -> "commands.undefined.usage");

    @Override
    @Nonnull
    @ZenMethod
    default String getUsage(ICommandSender sender) {
        return this.getCommandUsage.getCommandUsage(new ZenUtilsCommandSender(sender));
    }

    @ZenMethod
    default void register() {
        ZenCommandRegistrar.zenCommandMap.put(this.getName(), this);
    }
}
