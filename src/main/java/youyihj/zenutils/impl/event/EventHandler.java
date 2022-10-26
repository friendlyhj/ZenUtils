package youyihj.zenutils.impl.event;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.impl.util.FireEntityRemoveEventListener;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new FireEntityRemoveEventListener(event.getWorld()));
    }
}
