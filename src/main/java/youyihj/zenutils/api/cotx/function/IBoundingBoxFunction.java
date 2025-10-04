package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.aabb.MCAxisAlignedBB;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import crafttweaker.api.world.IBlockAccess;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@FunctionalInterface
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.IBoundingBoxFunction")
public interface IBoundingBoxFunction {
    @ZenMethod
    MCAxisAlignedBB apply(MCAxisAlignedBB origin, ICTBlockState blockState, IBlockAccess world, IBlockPos pos);
}
