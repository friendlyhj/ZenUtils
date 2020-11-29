package youyihj.zenutils.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.cotx.tile.TileEntityContent;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ITileEntityTick")
@ModOnly("contenttweaker")
@ZenRegister
@FunctionalInterface
public interface ITileEntityTick {
    void tick(TileEntityContent tileEntity, IWorld world, IBlockPos pos);
}
