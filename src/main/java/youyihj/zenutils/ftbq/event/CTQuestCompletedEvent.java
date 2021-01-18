package youyihj.zenutils.ftbq.event;

import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.ftbq.CTQuest;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.QuestCompletedEvent")
@ModOnly("ftbquests")
public class CTQuestCompletedEvent extends CTObjectCompletedEvent {
    private final ObjectCompletedEvent.QuestEvent event;

    public CTQuestCompletedEvent(ObjectCompletedEvent.QuestEvent event) {
        super(event);
        this.event = event;
    }

    @ZenGetter("quest")
    public CTQuest getQuest() {
        return new CTQuest(event.getQuest());
    }
}
