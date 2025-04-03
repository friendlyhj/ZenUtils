package youyihj.zenutils.impl.event;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.util.IEventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.util.InternalUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author youyihj
 */
public class GenericEventManagerImpl {
    @SuppressWarnings("unchecked")
    public static <T> void register(IEventHandler<T> eventHandler, EventPriority priority, boolean receiveCanceled, int busID) throws EventHandlerRegisterException {
        Class<T> eventType = getEventType(eventHandler);
        if (!Event.class.isAssignableFrom(eventType)) {
            registerCTEventHandler(eventHandler, eventType, priority, receiveCanceled, busID);
        } else {
            registerNativeEventHandler((IEventHandler<Event>) eventHandler, eventType.asSubclass(Event.class), priority, receiveCanceled, busID);
        }
    }

    private static <T> void registerCTEventHandler(IEventHandler<T> eventHandler, Class<T> typeOfT, EventPriority priority, boolean receiveCanceled, int busID) throws EventHandlerRegisterException {
        Class<? extends T> implementationClass = typeOfT.isInterface() ? findImplementationClass(typeOfT) : typeOfT;
        Constructor<? extends T> constructor = findProperCTEventConstructor(implementationClass);
        Class<?> forgeEventClass = constructor.getParameterTypes()[0];
        Event forgeEvent;
        try {
            forgeEvent = ((Event) forgeEventClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EventHandlerRegisterException("Failed to construct forge event", e);
        }
        try {
            CraftTweakerAPI.apply(new EventHandlerRegisterAction(new CTEventHandlerAdapter<>(eventHandler, constructor, receiveCanceled), forgeEvent.getListenerList(), priority, busID, typeOfT.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new EventHandlerRegisterException("Event constructor is not public", e);
        }
    }

    private static <T extends Event> void registerNativeEventHandler(IEventHandler<T> eventHandler, Class<T> typeOfT, EventPriority priority, boolean receiveCanceled, int busID) throws EventHandlerRegisterException {
        Event forgeEvent;
        try {
            forgeEvent = typeOfT.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EventHandlerRegisterException("Failed to construct forge event", e);
        }
        CraftTweakerAPI.apply(new EventHandlerRegisterAction(new CTNativeEventHandlerAdapter<>(eventHandler, receiveCanceled), forgeEvent.getListenerList(), priority, busID, typeOfT.getSimpleName()));
    }

    @SuppressWarnings({"unchecked"})
    private static <T> Class<T> getEventType(IEventHandler<T> eventHandler) throws EventHandlerRegisterException {
        Type tType = InternalUtils.getSingleItfGenericVariable(eventHandler.getClass(), IEventHandler.class);
        if (tType instanceof Class) {
            return (Class<T>) tType;
        }
        throw new EventHandlerRegisterException("The handler doesn't handle an actual event");
    }

    private static <T> Class<? extends T> findImplementationClass(Class<T> type) throws EventHandlerRegisterException {
        Set<ASMDataTable.ASMData> implementData = ZenUtils.asmDataTable.getAll(type.getTypeName().replace('.', '/'));
        if (implementData.size() == 1) {
            String implementClassName = implementData.iterator().next().getClassName().replace('/', '.');
            try {
                return Class.forName(implementClassName).asSubclass(type);
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new EventHandlerRegisterException("Forge said " + implementClassName + " implements " + type + ", but java doesn't think so.");
            }
        } else {
            for (ASMDataTable.ASMData asmData : implementData) {
                try {
                    Class<?> clazz = Class.forName(asmData.getClassName().replace('/', '.'));
                    if (!clazz.isInterface() && clazz.getInterfaces().length == 1 && clazz.getInterfaces()[0] == type) {
                        return clazz.asSubclass(type);
                    }
                } catch (ClassNotFoundException | ClassCastException ignored) {

                }
            }
        }

        throw new EventHandlerRegisterException("This event type doesn't have proper implementation class");
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findProperCTEventConstructor(Class<T> ctEventClass) throws EventHandlerRegisterException {
        for (Constructor<T> constructor : (Constructor<T>[]) ctEventClass.getConstructors()) {
            if (constructor.getParameterCount() == 1 && Event.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                return constructor;
            }
        }
        throw new EventHandlerRegisterException("The implementation class doesn't have proper constructor");
    }

    private static class CTEventHandlerAdapter<T> implements IEventListener {
        private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

        private final IEventHandler<T> handler;
        private final MethodHandle ctEventBuilder;
        private final boolean receiveCanceled;

        public CTEventHandlerAdapter(IEventHandler<T> handler, Constructor<? extends T> ctEventBuilder, boolean receiveCanceled) throws IllegalAccessException {
            this.handler = handler;
            this.ctEventBuilder = LOOKUP.unreflectConstructor(ctEventBuilder);
            this.receiveCanceled = receiveCanceled;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Event event) {
            if (event.isCancelable() && event.isCanceled() && !receiveCanceled) {
                return;
            }
            T ctEvent;
            try {
                ctEvent = (T) ctEventBuilder.invoke(event);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Failed to construct crt event", e);
                return;
            }
            try {
                handler.handle(ctEvent);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Exception occurred in the event handler", e);
            }
        }
    }

    private static class CTNativeEventHandlerAdapter<T extends Event> implements IEventListener {
        private final IEventHandler<T> handler;
        private final boolean receiveCanceled;

        public CTNativeEventHandlerAdapter(IEventHandler<T> handler, boolean receiveCanceled) {
            this.handler = handler;
            this.receiveCanceled = receiveCanceled;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Event event) {
            if (event.isCancelable() && event.isCanceled() && !receiveCanceled) {
                return;
            }
            try {
                handler.handle((T) event);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Exception occurred in the event handler", e);
            }
        }
    }
}
