package youyihj.zenutils.impl.util.catenation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import youyihj.zenutils.api.util.catenation.Catenation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class CatenationManager {
    static final Multimap<World, Catenation> catenations = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            CatenationManager.catenations.get(event.world).removeIf(it -> it.tick(new MCWorld(event.world)));
        }
    }
}
