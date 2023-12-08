package youyihj.zenutils.impl.event;

import com.google.common.reflect.TypeToken;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.util.IEventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.event.EventHandlerRegisterException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class GenericEventManagerImpl {
    private static final Type EVENT_HANDLER_TYPE_VARIABLE;
    private static final int MAIN_EVENT_BUS_ID = 0;

    static {
        try {
            EVENT_HANDLER_TYPE_VARIABLE = IEventHandler.class.getMethod("handle", Object.class).getGenericParameterTypes()[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void register(IEventHandler<T> eventHandler, EventPriority priority) throws EventHandlerRegisterException {
        Class<T> eventType = getEventType(eventHandler);
        Class<? extends T> implementationClass = findImplementationClass(eventType);
        Constructor<? extends T> constructor = findProperCTEventConstructor(implementationClass);
        Class<?> forgeEventClass = constructor.getParameterTypes()[0];
        Event forgeEvent;
        try {
            forgeEvent = ((Event) forgeEventClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EventHandlerRegisterException("Failed to construct forge event", e);
        }
        try {
            CraftTweakerAPI.apply(new EventHandlerRegisterAction(new CTEventHandlerAdapter<>(eventHandler, constructor), forgeEvent.getListenerList(), priority, MAIN_EVENT_BUS_ID, eventType.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new EventHandlerRegisterException("Event constructor is not public", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getEventType(IEventHandler<T> eventHandler) throws EventHandlerRegisterException {
        TypeToken<?> handlerType = TypeToken.of(eventHandler.getClass()).getSupertype(IEventHandler.class);
        Type tType = handlerType.resolveType(EVENT_HANDLER_TYPE_VARIABLE).getType();
        if (tType instanceof Class) {
            return (Class<T>) tType;
        }
        throw new EventHandlerRegisterException("The handler doesn't handle an actual event");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> findImplementationClass(Class<T> type) throws EventHandlerRegisterException {
        for (ASMDataTable.ASMData asmData : ZenUtils.asmDataTable.getAll(type.getTypeName().replace('.', '/'))) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName().replace('/', '.'));
                if (!clazz.isInterface() && clazz.getInterfaces().length == 1 && clazz.getInterfaces()[0] == type) {
                    return (Class<? extends T>) clazz;
                }
            } catch (Exception ignored) {}
        }
        throw new EventHandlerRegisterException("This event type doesn't have proper implementation class");
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findProperCTEventConstructor(Class<T> ctEventClass) throws EventHandlerRegisterException {
        for (Constructor<?> constructor : ctEventClass.getConstructors()) {
            if (constructor.getParameterCount() == 1 && Event.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                return (Constructor<T>) constructor;
            }
        }
        throw new EventHandlerRegisterException("The implementation class doesn't have proper constructor");
    }

    private static class CTEventHandlerAdapter<T> implements IEventListener {
        private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

        private final IEventHandler<T> handler;
        private final MethodHandle ctEventBuilder;

        public CTEventHandlerAdapter(IEventHandler<T> handler, Constructor<? extends T> ctEventBuilder) throws IllegalAccessException {
            this.handler = handler;
            this.ctEventBuilder = LOOKUP.unreflectConstructor(ctEventBuilder);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Event event) {
            try {
                T ctEvent = (T) ctEventBuilder.invoke(event);
                handler.handle(ctEvent);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Failed to construct crt event", e);
            }
        }
    }
}
