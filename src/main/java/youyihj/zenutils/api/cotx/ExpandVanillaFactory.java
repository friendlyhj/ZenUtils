package youyihj.zenutils.api.cotx;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.block.DirectionalBlockRepresentation;
import youyihj.zenutils.api.cotx.block.ExpandBlockRepresentation;
import youyihj.zenutils.api.cotx.function.ITileEntityTick;
import youyihj.zenutils.api.cotx.item.EnergyItemRepresentation;
import youyihj.zenutils.api.cotx.item.ExpandItemRepresentation;
import youyihj.zenutils.api.cotx.tile.TileEntityManager;
import youyihj.zenutils.api.cotx.tile.TileEntityRepresentation;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = ZenUtils.MOD_COT)
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
    public static DirectionalBlockRepresentation createDirectionalBlock(String unlocalizedName, IBlockMaterialDefinition blockMaterial, String directions) {
        return new DirectionalBlockRepresentation(unlocalizedName, blockMaterial, DirectionalBlockRepresentation.Directions.valueOf(directions));
    }

    @ZenMethodStatic
    public static ExpandItemRepresentation createExpandItem(String unlocalizedName) {
        return new ExpandItemRepresentation(unlocalizedName);
    }

    @ZenMethodStatic
    public static TileEntityRepresentation createActualTileEntity(int id) {
        return new TileEntityRepresentation(id);
    }

    @ZenMethodStatic
    public static void putTileEntityTickFunction(int id, ITileEntityTick tileEntityTick) {
        TileEntityManager.putTickFunction(id, tileEntityTick);
    }
}
