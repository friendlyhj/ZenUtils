package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Queue;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ICatenationTaskQueueBuilder")
public interface ICatenationTaskQueueBuilder {
    ICatenationTaskQueueBuilder addTask(ICatenationTask task);

    Queue<ICatenationTask> build();

    @ZenMethod
    ICatenationTaskQueueBuilder run(IWorldFunction function);

    @ZenMethod
    ICatenationTaskQueueBuilder sleep(long ticks);

    @ZenMethod
    ICatenationTaskQueueBuilder sleepUntil(IWorldCondition condition);

    @ZenMethod
    ICatenationTaskQueueBuilder customTimer(long duration, ITimerHandler handler);

    @ZenMethod
    ICatenationTaskQueueBuilder repeat(int times, ICatenationTaskQueueBuilderConsumer builderConsumer);

    @ZenMethod
    default ICatenationTaskQueueBuilder then(IWorldFunction function) {
        return run(function);
    }

    @ZenMethod
    default ICatenationTaskQueueBuilder alwaysUntil(IWorldCondition condition) {
        return sleepUntil(condition);
    }
}
