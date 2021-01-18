package youyihj.zenutils.ftbq;

import com.feed_the_beast.ftbquests.quest.reward.Reward;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * @author youyihj
 */
public class CTReward extends CTQuestObjectBase {
    private final Reward reward;

    public CTReward(Reward reward) {
        super(reward);
        this.reward = reward;
    }

    @ZenGetter("quest")
    public CTQuest getQuest() {
        return new CTQuest(reward.quest);
    }
}
