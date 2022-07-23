package youyihj.zenutils.api.util.delay;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.DelayRunnable")
@Deprecated
public interface DelayRunnable extends Runnable {

    @Override
    @ZenMethod
    void run();
}
