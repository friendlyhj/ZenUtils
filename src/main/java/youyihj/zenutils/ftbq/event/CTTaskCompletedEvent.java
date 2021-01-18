package youyihj.zenutils.ftbq.event;

import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.ftbq.CTTask;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.TaskCompletedEvent")
@ModOnly("ftbquests")
public class CTTaskCompletedEvent extends CTObjectCompletedEvent {
    public final ObjectCompletedEvent.TaskEvent event;

    public CTTaskCompletedEvent(ObjectCompletedEvent.TaskEvent event) {
        super(event);
        this.event = event;
    }

    @ZenGetter("task")
    public CTTask getTask() {
        return new CTTask(event.getTask());
    }
}
