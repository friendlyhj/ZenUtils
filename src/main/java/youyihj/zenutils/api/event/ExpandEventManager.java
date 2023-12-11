package youyihj.zenutils.api.event;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.event.EventHandlerRegisterException;
import youyihj.zenutils.impl.event.GenericEventManagerImpl;

/**
 * @author youyihj
 */
@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
public class ExpandEventManager {
    public static final EventList<EntityRemoveEvent> elEntityRemove = new EventList<>();
    public static final EventList<EntityItemFallEvent> elEntityItemFall = new EventList<>();
    public static final EventList<EntityItemDeathEvent> elEntityItemDeath = new EventList<>();
    public static final EventList<WorldLoadEvent> elWorldLoad = new EventList<>();
    public static final EventList<WorldUnloadEvent> elWorldUnload = new EventList<>();
    public static final EventList<WorldSaveEvent> elWorldSave = new EventList<>();

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

    @ZenMethod
    public static IEventHandle onWorldLoad(IEventManager manager, IEventHandler<WorldLoadEvent> ev) {
        return elWorldLoad.add(ev);
    }

    @ZenMethod
    public static IEventHandle onWorldUnload(IEventManager manager, IEventHandler<WorldUnloadEvent> ev) {
        return elWorldUnload.add(ev);
    }

    @ZenMethod
    public static IEventHandle onWorldSave(IEventManager manager, IEventHandler<WorldSaveEvent> ev) {
        return elWorldSave.add(ev);
    }

    @ZenMethod
    public static <T> void register(
            IEventManager manager,
            IEventHandler<T> eventHandler,
            @Optional(methodClass = CTEventPriority.class, methodName = "getDefault") CTEventPriority priority,
            @Optional boolean receiveCanceled
    ) {
        try {
            GenericEventManagerImpl.register(eventHandler, priority.getPriority(), receiveCanceled);
        } catch (EventHandlerRegisterException e) {
            CraftTweakerAPI.logError("Can not register this event handler", e);
        }
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

    @Mod.EventBusSubscriber
    public static class ForgeEventHandler {
        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event) {
            if (ExpandEventManager.elWorldLoad.hasHandlers()) {
                ExpandEventManager.elWorldLoad.publish(new WorldLoadEvent(event));
            }
        }

        @SubscribeEvent
        public static void onWorldUnload(WorldEvent.Unload event) {
            if (ExpandEventManager.elWorldUnload.hasHandlers()) {
                ExpandEventManager.elWorldUnload.publish(new WorldUnloadEvent(event));
            }
        }

        @SubscribeEvent
        public static void onWorldSave(WorldEvent.Save event) {
            if (ExpandEventManager.elWorldSave.hasHandlers()) {
                ExpandEventManager.elWorldSave.publish(new WorldSaveEvent(event));
            }
        }
    }
}
