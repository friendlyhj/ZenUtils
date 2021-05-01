package youyihj.zenutils.util.delay;

import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class DelayHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        long time = CraftTweaker.server.getEntityWorld().getWorldTime();
        DelayManager.DELAY_RUNNABLES.headMap(time, true).values().forEach(DelayRunnableList::run);
        synchronized (DelayHandler.class) {
            DelayManager.DELAY_RUNNABLES.keySet().removeIf(it -> it <= time);
        }
    }
}
