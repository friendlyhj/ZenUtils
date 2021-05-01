package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.NavigableMap;
import java.util.TreeMap;

@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class DelayManager {
    static final NavigableMap<Long, DelayRunnableList> DELAY_RUNNABLES = new TreeMap<>();

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        DELAY_RUNNABLES.putIfAbsent(delay, new DelayRunnableList());
        DELAY_RUNNABLES.get(delay).add(runnable);
    }
}
