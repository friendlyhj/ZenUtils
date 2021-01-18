package youyihj.zenutils.ftbq.event;

import com.feed_the_beast.ftbquests.events.TaskStartedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.ftbq.CTTask;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.TaskStartedEvent")
@ModOnly("ftbquests")
public class CTTaskStartedEvent {
    public CTTaskStartedEvent(TaskStartedEvent event) {
        this.event = event;
    }

    private final TaskStartedEvent event;

    @ZenGetter("task")
    public CTTask getTask() {
        return new CTTask(event.getTaskData().task);
    }
}
