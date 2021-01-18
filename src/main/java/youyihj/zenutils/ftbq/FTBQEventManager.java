package youyihj.zenutils.ftbq;

import com.feed_the_beast.ftbquests.events.CustomRewardEvent;
import com.feed_the_beast.ftbquests.events.CustomTaskEvent;
import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import com.feed_the_beast.ftbquests.events.TaskStartedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.ftbq.event.*;

/**
 * @author youyihj
 */
@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
@ModOnly("ftbquests")
@SuppressWarnings("unused")
public class FTBQEventManager {
    private static final EventList<CTTaskCompletedEvent> elTaskCompleted = new EventList<>();
    private static final EventList<CTQuestCompletedEvent> elQuestCompleted = new EventList<>();
    private static final EventList<CTChapterCompletedEvent> elChapterCompleted = new EventList<>();
    private static final EventList<CTTaskStartedEvent> elTaskStarted = new EventList<>();
    private static final EventList<CTCustomRewardEvent> elCustomReward = new EventList<>();
    private static final EventList<CTCustomTaskEvent> elCustomTask = new EventList<>();

    @ZenMethod
    public static IEventHandle onTaskCompleted(IEventManager manager, IEventHandler<CTTaskCompletedEvent> ev) {
        return elTaskCompleted.add(ev);
    }

    @ZenMethod
    public static IEventHandle onQuestCompleted(IEventManager manager, IEventHandler<CTQuestCompletedEvent> ev) {
        return elQuestCompleted.add(ev);
    }

    @ZenMethod
    public static IEventHandle onChapterCompleted(IEventManager manager, IEventHandler<CTChapterCompletedEvent> ev) {
        return elChapterCompleted.add(ev);
    }

    @ZenMethod
    public static IEventHandle onTaskStarted(IEventManager manager, IEventHandler<CTTaskStartedEvent> ev) {
        return elTaskStarted.add(ev);
    }

    @ZenMethod
    public static IEventHandle onCustomReward(IEventManager manager, IEventHandler<CTCustomRewardEvent> ev) {
        return elCustomReward.add(ev);
    }

    @ZenMethod
    public static IEventHandle onCustomTask(IEventManager manager, IEventHandler<CTCustomTaskEvent> ev) {
        return elCustomTask.add(ev);
    }

    @ZenMethod
    public static void clearFTBQEvents(IEventManager manager) {
        elTaskStarted.clear();
        elChapterCompleted.clear();
        elQuestCompleted.clear();
        elTaskCompleted.clear();
        elCustomReward.clear();
    }

    public static final class Handler {
        @SubscribeEvent
        public static void onTaskCompleted(ObjectCompletedEvent.TaskEvent event) {
            if (elTaskCompleted.hasHandlers()) {
                elTaskCompleted.publish(new CTTaskCompletedEvent(event));
            }
        }

        @SubscribeEvent
        public static void onQuestCompleted(ObjectCompletedEvent.QuestEvent event) {
            if (elQuestCompleted.hasHandlers()) {
                elQuestCompleted.publish(new CTQuestCompletedEvent(event));
            }
        }

        @SubscribeEvent
        public static void onChapterCompleted(ObjectCompletedEvent.ChapterEvent event) {
            if (elChapterCompleted.hasHandlers()) {
                elChapterCompleted.publish(new CTChapterCompletedEvent(event));
            }
        }

        @SubscribeEvent
        public static void onTaskStarted(TaskStartedEvent event) {
            if (elTaskStarted.hasHandlers()) {
                elTaskStarted.publish(new CTTaskStartedEvent(event));
            }
        }

        @SubscribeEvent
        public static void onCustomReward(CustomRewardEvent event) {
            if (elCustomReward.hasHandlers()) {
                elCustomReward.publish(new CTCustomRewardEvent(event));
            }
        }

        @SubscribeEvent
        public static void onCustomTask(CustomTaskEvent event) {
            if (elCustomTask.hasHandlers()) {
                elCustomTask.publish(new CTCustomTaskEvent(event));
            }
        }
    }
}
