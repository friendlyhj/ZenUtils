package youyihj.zenutils.api.ftbq;

import com.feed_the_beast.ftbquests.quest.task.Task;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ftbq.Task")
@ZenRegister
@ModOnly("ftbquests")
public class CTTask extends CTQuestObjectBase {
    private final Task task;

    public CTTask(Task task) {
        super(task);
        this.task = task;
    }

    @ZenGetter("quest")
    public CTQuest getQuest() {
        return new CTQuest(task.quest);
    }
}
