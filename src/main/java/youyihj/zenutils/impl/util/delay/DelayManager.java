package youyihj.zenutils.impl.util.delay;

import crafttweaker.mc1120.CraftTweaker;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import youyihj.zenutils.api.util.delay.DelayRunnable;

public class DelayManager {
    static final Long2ObjectRBTreeMap<DelayRunnableList> DELAY_RUNNABLES = new Long2ObjectRBTreeMap<>();

    public static void addDelayWork(DelayRunnable runnable, long delay) {
        long delayTime = CraftTweaker.server.getEntityWorld().getTotalWorldTime() + delay;
        DELAY_RUNNABLES.putIfAbsent(delayTime, new DelayRunnableList());
        DELAY_RUNNABLES.get(delayTime).add(runnable);
    }
}
