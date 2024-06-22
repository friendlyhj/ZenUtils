package youyihj.zenutils.api.cotx.block;

import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import youyihj.zenutils.api.cotx.tile.ExpandWorldForTile;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;
import youyihj.zenutils.api.cotx.tile.TileEntityRepresentation;
import youyihj.zenutils.api.world.ZenUtilsWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public abstract class OmniDirectionalBlockContent extends DirectionalBlockContent {
    protected OmniDirectionalBlockContent(DirectionalBlockRepresentation blockRepresentation) {
        super(blockRepresentation);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirectionalBlockRepresentation.Directions.ALL.getBlockProperty(), PLANE_ROTATION_PROPERTY);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        TileEntityRepresentation zsTE = getExpandBlockRepresentation().tileEntity;
        return new OmniDirectionalTileEntityContent(zsTE == null ? -1 : zsTE.getId(), getDirections().toMeta(state, true));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        IWorld ctWorld = CraftTweakerMC.getIWorld(world);
        IBlockPos ctPos = CraftTweakerMC.getIBlockPos(pos);
        TileEntityContent tileEntity = ExpandWorldForTile.getCustomTileEntity(ctWorld, ctPos);
        if (tileEntity != null) {
            IData customData = tileEntity.getCustomData();
            ZenUtilsWorld.catenation(ctWorld)
                         .then((w, ctx) -> {
                             if (CraftTweakerMC.getBlock(w.getBlock(ctPos)) == this) {
                                 TileEntityContent currentTE = ExpandWorldForTile.getCustomTileEntity(w, ctPos);
                                 if (currentTE != null) {
                                     currentTE.setCustomData(customData);
                                 }
                             }
                         })
                         .start();
        }
        super.breakBlock(world, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof OmniDirectionalTileEntityContent) {
            return getDirections().toState(((OmniDirectionalTileEntityContent) tileEntity).getRotationMeta(), state, true);
        }
        return state;
    }
}
