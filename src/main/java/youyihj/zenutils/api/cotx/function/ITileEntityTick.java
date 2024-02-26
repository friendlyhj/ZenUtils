package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = ZenUtils.MOD_COT)
@ZenClass("mods.zenutils.cotx.ITileEntityTick")
@FunctionalInterface
public interface ITileEntityTick {
    void tick(TileEntityContent tileEntity, IWorld world, IBlockPos pos);
}
