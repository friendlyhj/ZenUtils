package youyihj.zenutils.api.event;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.event.GenericEventManagerImpl;

/**
 * @author youyihj
 */
@ZenClass
@ZenExpansion("crafttweaker.event.IEventManager")
public class GenericEventManager {
    @ZenMethod
    public <T> void register(IEventManager manager, IEventHandler<T> eventHandler, @Optional(methodClass = CTEventPriority.class, methodName = "getDefault") CTEventPriority priority) {
        try {
            GenericEventManagerImpl.register(eventHandler, priority.getPriority());
        } catch (EventHandlerRegisterException e) {
            CraftTweakerAPI.logError("Can not register this event handler", e);
        }
    }
}
