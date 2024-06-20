package youyihj.zenutils.api.cotx.tile;


import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import crafttweaker.CraftTweakerAPI;
import youyihj.zenutils.api.cotx.function.ITileEntityTick;

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
        putTickFunction(id, tileEntityRepresentation.onTick);
    }

    public static ITileEntityTick getTickFunction(int id) {
        return TICK_FUNCTIONS.getOrDefault(id, ((tileEntity, world, pos) -> {}));
    }

    public static void putTickFunction(int id, ITileEntityTick tileEntityTick) {
        Preconditions.checkArgument(id != -1, "id -1 is reserved");
        TICK_FUNCTIONS.put(id, tileEntityTick);
    }
}
