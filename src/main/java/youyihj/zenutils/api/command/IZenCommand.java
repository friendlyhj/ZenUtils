package youyihj.zenutils.api.command;

import net.minecraft.command.ICommand;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@SuppressWarnings("unused")
public interface IZenCommand extends ICommand {
    @ZenMethod
    default void register() {
        ZenCommandRegistrar.zenCommandMap.put(this.getName(), this);
    }
}
