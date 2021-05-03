package youyihj.zenutils.impl.util.delay;

import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class DelayHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        long time = CraftTweaker.server.getEntityWorld().getTotalWorldTime();
        DelayManager.DELAY_RUNNABLES.headMap(time).values().forEach(DelayRunnableList::run);
        if (time % 400 == 0) {
            synchronized (DelayHandler.class) {
                DelayManager.DELAY_RUNNABLES.keySet().removeIf(it -> it <= time);
            }
        }
    }
}
