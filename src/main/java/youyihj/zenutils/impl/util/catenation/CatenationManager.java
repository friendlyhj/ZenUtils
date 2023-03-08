package youyihj.zenutils.impl.util.catenation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.CatenationStatus;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class CatenationManager {
    private static final Multimap<World, Catenation> catenations = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
    private static final Multimap<World, Catenation> cantenationsToAdd = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    private static final List<Catenation> clientCatenations = new ArrayList<>();
    private static final List<Catenation> clientCatenationsToAdd = new ArrayList<>();

    public static void addCatenation(World world, Catenation catenation) {
        cantenationsToAdd.put(world, catenation);
    }

    public static void addClientCatenation(Catenation catenation) {
        clientCatenationsToAdd.add(catenation);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        switch (event.phase) {
            case START:
                CatenationManager.catenations.get(event.world).removeIf(it -> it.tick(new MCWorld(event.world)));
                break;
            case END:
                catenations.putAll(cantenationsToAdd);
                cantenationsToAdd.clear();
                break;
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        catenations.entries().forEach(it -> it.getValue().getContext().setStatus(CatenationStatus.UNLOAD, new MCWorld(it.getKey())));
        List<Catenation> unfinished = ImmutableList.<Catenation>builder().addAll(catenations.get(world)).addAll(cantenationsToAdd.get(world)).build();
        CatenationPersistenceImpl.onWorldUnload(CraftTweakerMC.getIWorld(world), unfinished);
        catenations.removeAll(world);
        cantenationsToAdd.removeAll(world);
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world != null) {
                switch (event.phase) {
                    case START:
                        CatenationManager.clientCatenations.removeIf(it -> it.tick(new MCWorld(world)));
                        break;
                    case END:
                        clientCatenations.addAll(clientCatenationsToAdd);
                        clientCatenationsToAdd.clear();
                        break;
                }
            } else if (!clientCatenations.isEmpty()) {
                clientCatenations.forEach(it -> it.getContext().setStatus(CatenationStatus.UNLOAD, it.getWorld()));
                clientCatenations.clear();
                clientCatenationsToAdd.clear();
            }
        }
    }
}
