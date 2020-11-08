package youyihj.zenutils.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IEntityCollided")
public interface IEntityCollided {
    void call(IWorld world, IBlockPos pos, ICTBlockState state, IEntity entity);
}
