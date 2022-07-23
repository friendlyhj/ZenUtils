package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.Queue;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.Catenation")
public class Catenation {
    private final Queue<ICatenationTask> tasks;
    private boolean stopped;
    @Nullable
    private final IWorldCondition stopWhen;

    public Catenation(Queue<ICatenationTask> tasks, @Nullable IWorldCondition stopWhen) {
        this.tasks = tasks;
        this.stopWhen = stopWhen;
    }

    @ZenMethod
    public boolean tick(IWorld world) {
        if (stopWhen != null && stopWhen.apply(world)) {
            stopped = true;
        }
        if (stopped) {
            return true;
        }
        ICatenationTask task = tasks.peek();
        if (task == null) return true;
        task.run(world);
        if (task.isComplete()) {
            tasks.poll();
        }
        return false;
    }

    @ZenMethod
    public void stop() {
        stopped = true;
    }

    @ZenGetter("stopped")
    @ZenMethod
    public boolean isStopped() {
        return stopped;
    }
}
