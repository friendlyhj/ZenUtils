package youyihj.zenutils.ftbq;

import com.feed_the_beast.ftbquests.quest.Chapter;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ftbq.Chapter")
@ZenRegister
@ModOnly("ftbquests")
public class CTChapter extends CTQuestObjectBase {
    private final Chapter chapter;

    public CTChapter(Chapter chapter) {
        super(chapter);
        this.chapter = chapter;
    }

    @ZenGetter("quests")
    public List<CTQuest> getQuests() {
        return chapter.quests.stream().map(CTQuest::new).collect(Collectors.toList());
    }
}
