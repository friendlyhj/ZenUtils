package youyihj.zenutils.api.cotx.tile;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraft.tileentity.TileEntity;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.world.IWorld")
@ModOnly("contenttweaker")
public class ExpandWorldForTile {
    @ZenMethod
    @Nullable
    public static TileEntityContent getCustomTileEntity(IWorld world, IBlockPos pos) {
        TileEntity tileEntity = CraftTweakerMC.getWorld(world).getTileEntity(CraftTweakerMC.getBlockPos(pos));
        if (tileEntity instanceof TileEntityContent) {
            return (TileEntityContent) tileEntity;
        }
        return null;
    }
}
