package youyihj.zenutils.cotx.function;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IEntityWalk")
@ModOnly("contenttweaker")
public interface IEntityWalk {
    void call(IWorld world, IBlockPos pos, IEntity entity);
}
