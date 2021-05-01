package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.CraftTweaker;
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
        long delayTime = CraftTweaker.server.getEntityWorld().getWorldTime() + delay;
        DELAY_RUNNABLES.putIfAbsent(delayTime, new DelayRunnableList());
        DELAY_RUNNABLES.get(delayTime).add(runnable);
    }
}
