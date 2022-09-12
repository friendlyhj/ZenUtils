package youyihj.zenutils.api.reload;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author youyihj
 */
public class ScriptReloadEvent extends Event {
    public static class Pre extends ScriptReloadEvent {}

    public static class Post extends ScriptReloadEvent {}
}
