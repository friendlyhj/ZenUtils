package youyihj.zenutils.cotx.tile;


import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.cotx.function.ITileEntityTick;

import java.util.Map;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public final class TileEntityManager {
    private static final Map<Integer, ITileEntityTick> TICK_FUNCTIONS = Maps.newHashMap();

    public static void registerTileEntity(TileEntityRepresentation tileEntityRepresentation) {
        TICK_FUNCTIONS.put(tileEntityRepresentation.getId(), tileEntityRepresentation.onTick);
    }

    public static ITileEntityTick getTickFunction(int id) {
        return TICK_FUNCTIONS.getOrDefault(id, ((tileEntity, world, pos) -> {}));
    }

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityContent.class, new ResourceLocation(ZenUtils.MODID, "tile_entity"));
    }
}
