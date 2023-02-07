package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
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
    ICatenationBuilder stopWhen(IWorldCondition condition);

    @ZenMethod
    ICatenationBuilder onStop(IWorldFunction function);

    @ZenMethod
    default ICatenationBuilder then(IWorldFunction function) {
        return run(function);
    }

    @ZenMethod
    Catenation start();
}
