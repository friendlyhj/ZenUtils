package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@FunctionalInterface
@SidedZenRegister(modDeps = ZenUtils.MOD_COT)
@ZenClass("mods.zenutils.cotx.IPlacementChecker")
public interface IPlacementChecker {
    boolean canPlaceAt(IWorld world, IBlockPos pos);
}
