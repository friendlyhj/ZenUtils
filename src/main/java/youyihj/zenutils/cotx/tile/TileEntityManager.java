package youyihj.zenutils.cotx.tile;


import com.google.common.collect.Maps;
import crafttweaker.CraftTweakerAPI;
import youyihj.zenutils.cotx.function.ITileEntityTick;

import java.util.Map;

/**
 * @author youyihj
 */
public final class TileEntityManager {
    private static final Map<Integer, ITileEntityTick> TICK_FUNCTIONS = Maps.newHashMap();

    public static void registerTileEntity(TileEntityRepresentation tileEntityRepresentation) {
        int id = tileEntityRepresentation.getId();
        if (TICK_FUNCTIONS.containsKey(id)) {
            CraftTweakerAPI.logError("Tile Entity ID: " + id + " has been used!");
            return;
        }
        TICK_FUNCTIONS.put(id, tileEntityRepresentation.onTick);
    }

    public static ITileEntityTick getTickFunction(int id) {
        return TICK_FUNCTIONS.getOrDefault(id, ((tileEntity, world, pos) -> {}));
    }
}
