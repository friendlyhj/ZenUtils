package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraft.entity.item.EntityItem;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
public class ExpandEventManager {
    public static final EventList<EntityRemoveEvent> elEntityRemove = new EventList<>();
    public static final EventList<EntityItemFallEvent> elEntityItemFall = new EventList<>();

    @ZenMethod
    public static IEventHandle onEntityRemove(IEventManager manager, IEventHandler<EntityRemoveEvent> ev) {
        return elEntityRemove.add(ev);
    }

    @ZenMethod
    public static IEventHandle onEntityItemFall(IEventManager manager, IEventHandler<EntityItemFallEvent> ev) {
        return elEntityItemFall.add(ev);
    }

    public static void handleEntityItemFallEvent(EntityItem entityItem, float distance) {
        if (ExpandEventManager.elEntityItemFall.hasHandlers()) {
            ExpandEventManager.elEntityItemFall.publish(new EntityItemFallEvent(entityItem, distance));
        }
    }
}
