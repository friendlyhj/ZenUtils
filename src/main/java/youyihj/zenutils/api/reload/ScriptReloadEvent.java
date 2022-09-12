package youyihj.zenutils.api.reload;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author youyihj
 */
public class ScriptReloadEvent extends Event {
    private final ICommandSender requester;

    public ScriptReloadEvent(ICommandSender reloadRequester) {
        this.requester = reloadRequester;
    }

    public ICommandSender getRequester() {
        return requester;
    }

    public static class Pre extends ScriptReloadEvent {
        public Pre(ICommandSender reloadRequester) {
            super(reloadRequester);
        }
    }

    public static class Post extends ScriptReloadEvent {
        public Post(ICommandSender reloadRequester) {
            super(reloadRequester);
        }
    }
}
