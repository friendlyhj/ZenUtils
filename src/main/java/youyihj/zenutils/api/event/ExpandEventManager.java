package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
public class ExpandEventManager {
    public static final EventList<EntityRemoveEvent> elEntityRemove = new EventList<>();

    @ZenMethod
    public static IEventHandle onEntityRemove(IEventManager manager, IEventHandler<EntityRemoveEvent> ev) {
        return elEntityRemove.add(ev);
    }
}
