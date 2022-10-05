package youyihj.zenutils.api.command;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import net.minecraft.command.CommandHandler;
import youyihj.zenutils.api.reload.Reloadable;
import youyihj.zenutils.api.util.ReflectionInvoked;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author youyihj
 */
@Reloadable
public class ZenCommandRegisterAction implements IAction {
    private final IZenCommand command;

    public ZenCommandRegisterAction(IZenCommand command) {
        this.command = command;
    }

    @Override
    public void apply() {
        Objects.requireNonNull(ApplyLogic.INSTANCE.commandHandler).registerCommand(command);
    }

    @ReflectionInvoked
    public void undo() {
        CommandHandler commandHandler = ApplyLogic.INSTANCE.getCommandHandler();
        commandHandler.getCommands().remove(command.getName());
        commandHandler.commandSet.remove(command);
        ApplyLogic.INSTANCE.toApplyActions.remove(this);
    }

    @Override
    public String describe() {
        return "Registering ZenCommand " + command.getName();
    }

    public enum ApplyLogic {
        INSTANCE;

        private final List<ZenCommandRegisterAction> toApplyActions = new LinkedList<>();
        private CommandHandler commandHandler;

        public void apply(ZenCommandRegisterAction action) {
            toApplyActions.add(action);
            if (commandHandler != null) {
                CraftTweakerAPI.apply(action);
            }
        }

        public void init(CommandHandler commandHandler) {
            this.commandHandler = commandHandler;
            toApplyActions.forEach(CraftTweakerAPI::apply);
        }

        public void clean() {
            this.commandHandler = null;
        }

        public CommandHandler getCommandHandler() {
            return commandHandler;
        }
    }
}
