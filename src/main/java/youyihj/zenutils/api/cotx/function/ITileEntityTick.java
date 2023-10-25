package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.cotx.ITileEntityTick")
@FunctionalInterface
public interface ITileEntityTick {
    void tick(TileEntityContent tileEntity, IWorld world, IBlockPos pos);
}
