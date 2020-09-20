package youyihj.zenutils.cotx;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.cotx.block.ExpendBlockRepresentation;
import youyihj.zenutils.cotx.item.EnergyItemRepresentation;
import youyihj.zenutils.cotx.item.ExpandItemRepresentation;

@ZenRegister
@ModOnly("contenttweaker")
@ZenExpansion("mods.contenttweaker.VanillaFactory")
public class ExpandVanillaFactory {
    @ZenMethodStatic
    public static EnergyItemRepresentation createEnergyItem(String unlocalizedName, int capacity, int maxReceive, int maxExtract) {
        return new EnergyItemRepresentation(unlocalizedName, capacity, maxReceive, maxExtract);
    }

    @ZenMethodStatic
    public static ExpendBlockRepresentation createExpendBlock(String unlocalizedName, IBlockMaterialDefinition blockMaterial) {
        return new ExpendBlockRepresentation(unlocalizedName, blockMaterial);
    }

    @ZenMethodStatic
    public static ExpandItemRepresentation createExpendItem(String unlocalizedName) {
        return new ExpandItemRepresentation(unlocalizedName);
    }
}
