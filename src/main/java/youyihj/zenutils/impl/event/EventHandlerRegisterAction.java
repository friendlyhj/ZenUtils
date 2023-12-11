package youyihj.zenutils.impl.event;

import crafttweaker.IAction;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import youyihj.zenutils.api.reload.Reloadable;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@Reloadable
public class EventHandlerRegisterAction implements IAction {
    private final IEventListener listener;
    private final ListenerList listenerList;
    private final EventPriority priority;
    private final int busID;
    private final String eventName;

    public EventHandlerRegisterAction(IEventListener listener, ListenerList listenerList, EventPriority priority, int busID, String eventName) {
        this.listener = listener;
        this.listenerList = listenerList;
        this.priority = priority;
        this.busID = busID;
        this.eventName = eventName;
    }

    @Override
    public void apply() {
        listenerList.register(busID, priority, listener);
    }

    @ReflectionInvoked
    public void undo() {
        listenerList.unregister(busID, listener);
    }

    @Override
    public String describe() {
        return "Registering an event listener for " + eventName;
    }
}
