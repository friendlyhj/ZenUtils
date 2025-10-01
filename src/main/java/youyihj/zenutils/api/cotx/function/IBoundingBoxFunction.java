package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.aabb.MCAxisAlignedBB;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import crafttweaker.api.world.IBlockAccess;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
public interface IBoundingBoxFunction {
    @ZenMethod
    MCAxisAlignedBB apply(MCAxisAlignedBB origin, ICTBlockState blockState, IBlockAccess world, IBlockPos pos);
}
