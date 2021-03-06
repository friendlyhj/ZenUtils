package youyihj.zenutils.api.util.delay;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.delay.DelayManager;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class CTDelayManager {

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        DelayManager.addDelayWork(runnable, IsExecute.of(true), delay);
    }

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, IsExecute isExecute, @Optional(valueLong = 1L) long delay) {
        DelayManager.addDelayWork(runnable, isExecute, delay);
    }
}
