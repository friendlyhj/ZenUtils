package youyihj.zenutils.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.cotx.IBlockActivated")
public interface IBlockActivated {
    boolean activate(IWorld world, IBlockPos pos, IBlockState state, IPlayer player, Hand hand, Facing facing, float[] blockHit);
}
