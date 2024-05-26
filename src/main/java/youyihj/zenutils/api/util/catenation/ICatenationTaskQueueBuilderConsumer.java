package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ICatenationTaskQueueBuilderConsumer")
public interface ICatenationTaskQueueBuilderConsumer {
    @ZenMethod
    void apply(ICatenationTaskQueueBuilder builder);
}
