package youyihj.zenutils.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IEntityWalk")
@ModOnly("contenttweaker")
public interface IEntityWalk {
    void call(IWorld world, IBlockPos pos, IEntity entity);
}
