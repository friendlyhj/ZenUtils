package youyihj.zenutils.cotx.function;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IEntityCollided")
public interface IEntityCollided {
    void call(IWorld world, IBlockPos pos, IBlockState state, IEntity entity);
}
