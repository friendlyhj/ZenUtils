package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.CraftTweaker;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class DelayManager {
    static final Long2ObjectRBTreeMap<DelayRunnableList> DELAY_RUNNABLES = new Long2ObjectRBTreeMap<>();

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        long delayTime = CraftTweaker.server.getEntityWorld().getTotalWorldTime() + delay;
        DELAY_RUNNABLES.putIfAbsent(delayTime, new DelayRunnableList());
        DELAY_RUNNABLES.get(delayTime).add(runnable);
    }
}
