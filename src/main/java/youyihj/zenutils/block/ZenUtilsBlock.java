package youyihj.zenutils.block;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.block.IBlock")
@SuppressWarnings("unused")
public class ZenUtilsBlock {
    @ZenMethod
    @SuppressWarnings("deprecation")
    public static IItemStack getItem(IBlock block, IWorld world, IBlockPos pos, IBlockState state) {
        return CraftTweakerMC.getIItemStack(CraftTweakerMC.getBlock(block).getItem(
                CraftTweakerMC.getWorld(world),
                CraftTweakerMC.getBlockPos(pos),
                CraftTweakerMC.getBlockState(state)
        ));
    }
}
