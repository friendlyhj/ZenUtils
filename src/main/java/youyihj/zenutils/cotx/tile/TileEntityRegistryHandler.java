package youyihj.zenutils.cotx.tile;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class TileEntityRegistryHandler {
    @SubscribeEvent
    public static void handle(RegistryEvent.Register<Block> event) {
        if (Loader.isModLoaded("contenttweaker")) {
            GameRegistry.registerTileEntity(TileEntityContent.class, new ResourceLocation(ZenUtils.MODID, "tile_entity"));
        }
    }
}
