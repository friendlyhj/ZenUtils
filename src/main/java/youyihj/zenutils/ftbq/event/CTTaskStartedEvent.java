package youyihj.zenutils.ftbq.event;

import com.feed_the_beast.ftbquests.events.TaskStartedEvent;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.ftbq.CTTask;

/**
 * @author youyihj
 */
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
