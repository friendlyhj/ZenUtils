package youyihj.zenutils.api.util.catenation;

import crafttweaker.CraftTweakerAPI;
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
    private final CatenationContext context = new CatenationContext(this);

    public Catenation(Queue<ICatenationTask> tasks, @Nullable IWorldCondition stopWhen) {
        this.tasks = tasks;
        this.stopWhen = stopWhen;
    }

    @ZenMethod
    public boolean tick(IWorld world) {
        if (stopWhen != null) {
            try {
                if (stopWhen.apply(world, context)) {
                    stopped = true;
                }
            } catch (Exception exception) {
                CraftTweakerAPI.logError("Exception occurred in stopWhen function, stopping the catenation...", exception);
                stopped = true;
            }
        }
        if (stopped) {
            return true;
        }
        ICatenationTask task = tasks.peek();
        if (task == null) return true;
        try {
            task.run(world, context);
        } catch (Exception exception) {
            CraftTweakerAPI.logError("Exception occurred in a catenation task, stopping the catenation...", exception);
            return true;
        }
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

    @ZenGetter("context")
    @ZenMethod
    public CatenationContext getContext() {
        return context;
    }
}
