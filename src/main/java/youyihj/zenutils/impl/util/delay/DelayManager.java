package youyihj.zenutils.impl.util.delay;

import crafttweaker.mc1120.CraftTweaker;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.delay.DelayRunnable;

public class DelayManager {
    static final Long2ObjectRBTreeMap<DelayRunnableList> DELAY_RUNNABLES = new Long2ObjectRBTreeMap<>();

    public static void addDelayWork(DelayRunnable runnable, long delay, String uid) {
        long delayTime = CraftTweaker.server.getEntityWorld().getTotalWorldTime() + delay;
        DELAY_RUNNABLES.putIfAbsent(delayTime, new DelayRunnableList(uid));
        DELAY_RUNNABLES.get(delayTime).add(runnable);
    }

    @ZenMethod
    public static void delDelayWork(String uid){
        DELAY_RUNNABLES.forEach((k,v) -> {
            if(v.uid.equals(uid)){
                DELAY_RUNNABLES.remove(k,v);
            }
        });
    }
}
