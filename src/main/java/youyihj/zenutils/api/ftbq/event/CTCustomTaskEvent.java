package youyihj.zenutils.api.ftbq.event;

import com.feed_the_beast.ftbquests.events.CustomTaskEvent;
import com.feed_the_beast.ftbquests.quest.task.CustomTask;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;
import youyihj.zenutils.api.ftbq.CTTask;

import javax.annotation.Nonnull;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.CustomTaskEvent")
@ModOnly("ftbquests")
@SuppressWarnings("unused")
public class CTCustomTaskEvent implements IEventCancelable {
    private final CustomTaskEvent event;

    public CTCustomTaskEvent(CustomTaskEvent event) {
        this.event = event;
    }

    @ZenGetter("task")
    public CTTask getTask() {
        return new CTTask(event.getTask());
    }

    @ZenGetter("checkTimer")
    public int getCheckTimer() {
        return event.getTask().checkTimer;
    }

    @ZenSetter("checkTimer")
    public void setCheckTimer(int time) {
        event.getTask().checkTimer = time;
    }

    @ZenSetter("checker")
    public void setChecker(ITaskChecker checker) {
        event.getTask().check = new TaskCheckWrapper(checker);
    }

    @ZenGetter("maxProgress")
    public int getMaxProgress() {
        return Math.toIntExact(event.getTask().maxProgress);
    }

    @ZenSetter("maxProgress")
    public void setMaxProgress(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("max progress must be bigger than 0");
        }
        event.getTask().maxProgress = max;
    }

    @ZenGetter
    public boolean enableButton() {
        return event.getTask().enableButton;
    }

    @ZenSetter
    public void enableButton(boolean b) {
        event.getTask().enableButton = b;
    }

    @Override
    public boolean isCanceled() {
        return event.isCanceled();
    }

    @Override
    public void setCanceled(boolean canceled) {
        event.setCanceled(canceled);
    }

    @ZenRegister
    @ZenClass("mods.zenutils.ftbq.ITaskChecker")
    @FunctionalInterface
    @ModOnly("ftbquests")
    public interface ITaskChecker {
        int check(IPlayer player, int currentProgress);
    }

    private static class TaskCheckWrapper implements CustomTask.Check {

        private final ITaskChecker checker;

        TaskCheckWrapper(ITaskChecker checker) {
            this.checker = checker;
        }

        @Override
        public void check(@Nonnull CustomTask.Data taskData, @Nonnull EntityPlayerMP player) {
            taskData.setProgress(this.checker.check(CraftTweakerMC.getIPlayer(player), Math.toIntExact(taskData.progress)));
        }
    }
}
