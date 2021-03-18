package youyihj.zenutils.util.delay;

import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class DelayHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        long time = CraftTweaker.server.getEntityWorld().getWorldTime();
        DelayManager.DELAY_RUNNABLE_LIST.stream().filter(pair -> pair.getRight() >= time).map(Pair::getLeft).forEach(DelayRunnable::run);
        DelayManager.DELAY_RUNNABLE_LIST.removeIf(pair -> pair.getRight() >= time);
    }
}
