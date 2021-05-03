package youyihj.zenutils.api.ftbq;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObjectBase;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.task.Task;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.Quest")
@ModOnly("ftbquests")
public class CTQuest extends CTQuestObjectBase{
    public CTQuest(Quest quest) {
        super(quest);
        this.quest = quest;
    }

    private final Quest quest;

    @ZenGetter("chapter")
    public CTChapter getChapter() {
        return new CTChapter(quest.getChapter());
    }

    @ZenGetter("x")
    public double getX() {
        return quest.getX();
    }

    @ZenGetter("y")
    public double getY() {
        return quest.getY();
    }

    @ZenGetter("width")
    public double getWidth() {
        return quest.getWidth();
    }

    @ZenGetter("height")
    public double getHeight() {
        return quest.getHeight();
    }

    @ZenGetter("tasks")
    public List<CTTask> getTasks() {
        return quest.tasks.stream().map(CTTask::new).collect(Collectors.toList());
    }

    @ZenGetter("rewards")
    public List<CTReward> getRewards() {
        return quest.rewards.stream().map(CTReward::new).collect(Collectors.toList());
    }

    @ZenGetter("dependencies")
    public List<CTQuestObjectBase> getDependencies() {
        return quest.dependencies.stream().map(CTQuest::getQuestObject).collect(Collectors.toList());
    }

    @ZenGetter("subtitle")
    public String getSubtitle() {
        return quest.getSubtitle();
    }

    @ZenGetter("description")
    public String[] getDescription() {
        return quest.getDescription();
    }

    @ZenGetter("shape")
    public String getShape() {
        return quest.getShape();
    }

    @ZenGetter
    public boolean canRepeat() {
        return quest.canRepeat;
    }

    @ZenGetter
    public String disableJEI() {
        return quest.disableJEI.getName();
    }

    private static CTQuestObjectBase getQuestObject(QuestObjectBase obj) {
        if (obj == null)
            return null;
        if (obj instanceof Task) {
            return new CTTask(((Task) obj));
        }
        if (obj instanceof Quest) {
            return new CTQuest((Quest) obj);
        }
        if (obj instanceof Chapter) {
            return new CTChapter((Chapter) obj);
        }
        if (obj instanceof Reward) {
            return new CTReward((Reward) obj);
        }
        return new CTQuestObjectBase(obj);
    }
}
