package youyihj.zenutils.api.block;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethodStatic;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.block.IBlockDefinition")
public class ZenUtilsBlockDefinition {
    @ZenGetter("numericalId")
    public static int getNumericalId(IBlockDefinition definition) {
        return Block.getIdFromBlock(CraftTweakerMC.getBlock(definition));
    }

    @ZenMethodStatic
    public static IBlockDefinition getFromNumericalId(int id) {
        return CraftTweakerMC.getBlockDefinition(Block.getBlockById(id));
    }
}
