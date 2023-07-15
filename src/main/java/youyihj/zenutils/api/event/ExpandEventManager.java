package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
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
    public static final EventList<EntityItemDeathEvent> elEntityItemDeath = new EventList<>();

    @ZenMethod
    public static IEventHandle onEntityRemove(IEventManager manager, IEventHandler<EntityRemoveEvent> ev) {
        return elEntityRemove.add(ev);
    }

    @ZenMethod
    public static IEventHandle onEntityItemFall(IEventManager manager, IEventHandler<EntityItemFallEvent> ev) {
        return elEntityItemFall.add(ev);
    }

    @ZenMethod
    public static IEventHandle onEntityItemDeath(IEventManager manager, IEventHandler<EntityItemDeathEvent> ev) {
        return elEntityItemDeath.add(ev);
    }

    public static void handleEntityItemFallEvent(EntityItem entityItem, float distance) {
        if (ExpandEventManager.elEntityItemFall.hasHandlers()) {
            ExpandEventManager.elEntityItemFall.publish(new EntityItemFallEvent(entityItem, distance));
        }
    }

    public static void handleEntityItemDeathEvent(EntityItem entityItem, DamageSource damageSource) {
        if (ExpandEventManager.elEntityItemDeath.hasHandlers()) {
            ExpandEventManager.elEntityItemDeath.publish(new EntityItemDeathEvent(entityItem, damageSource));
        }
    }
}
