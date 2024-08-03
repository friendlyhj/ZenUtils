package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ICatenationBuilder")
public interface ICatenationBuilder {
    ICatenationBuilder addTask(ICatenationTask task);

    @ZenMethod
    ICatenationBuilder run(IWorldFunction function);

    @ZenMethod
    ICatenationBuilder sleep(long ticks);

    @ZenMethod
    ICatenationBuilder sleepUntil(IWorldCondition condition);

    @ZenMethod
    ICatenationBuilder customTimer(long duration, ITimerHandler handler);

    @ZenMethod
    ICatenationBuilder repeat(int times, ICatenationTaskQueueBuilderConsumer builderConsumer);

    @ZenMethod
    ICatenationBuilder stopWhen(IWorldCondition condition);

    @ZenMethod
    ICatenationBuilder onStop(IWorldFunction function);

    @ZenMethod
    ICatenationBuilder data(IData data);

    @ZenMethod
    default ICatenationBuilder then(IWorldFunction function) {
        return run(function);
    }

    @ZenMethod
    default ICatenationBuilder alwaysUntil(IWorldCondition condition) {
        return sleepUntil(condition);
    }

    @ZenMethod
    Catenation start();
}
