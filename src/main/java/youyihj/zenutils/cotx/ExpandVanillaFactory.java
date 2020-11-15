package youyihj.zenutils.cotx;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.cotx.block.ExpandBlockRepresentation;
import youyihj.zenutils.cotx.item.EnergyItemRepresentation;
import youyihj.zenutils.cotx.item.ExpandItemRepresentation;
import youyihj.zenutils.cotx.tile.TileEntityManager;
import youyihj.zenutils.cotx.tile.TileEntityRepresentation;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@ZenExpansion("mods.contenttweaker.VanillaFactory")
public class ExpandVanillaFactory {
    @ZenMethodStatic
    public static EnergyItemRepresentation createEnergyItem(String unlocalizedName, int capacity, int maxReceive, int maxExtract) {
        return new EnergyItemRepresentation(unlocalizedName, capacity, maxReceive, maxExtract);
    }

    @ZenMethodStatic
    public static ExpandBlockRepresentation createExpandBlock(String unlocalizedName, IBlockMaterialDefinition blockMaterial) {
        return new ExpandBlockRepresentation(unlocalizedName, blockMaterial);
    }

    @ZenMethodStatic
    public static ExpandItemRepresentation createExpandItem(String unlocalizedName) {
        return new ExpandItemRepresentation(unlocalizedName);
    }

    @ZenMethodStatic
    public static TileEntityRepresentation createActualTileEntity(int id) {
        TileEntityManager.enable();
        return new TileEntityRepresentation(id);
    }
}
