package youyihj.zenutils.api.ftbq.event;

import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.api.ftbq.CTChapter;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.ChapterCompletedEvent")
@ModOnly("ftbquests")
public class CTChapterCompletedEvent extends CTObjectCompletedEvent {
    private final ObjectCompletedEvent.ChapterEvent event;

    public CTChapterCompletedEvent(ObjectCompletedEvent.ChapterEvent event) {
        super(event);
        this.event = event;
    }

    @ZenGetter("chapter")
    public CTChapter getChapter() {
        return new CTChapter(event.getChapter());
    }
}
