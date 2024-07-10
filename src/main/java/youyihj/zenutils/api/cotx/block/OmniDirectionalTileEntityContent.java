package youyihj.zenutils.api.cotx.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;
import youyihj.zenutils.api.util.ReflectionInvoked;

import javax.annotation.Nonnull;

/**
 * @author youyihj
 */
public class OmniDirectionalTileEntityContent extends TileEntityContent {
    private int rotationMeta;
    private static final String TAG_ROTATION = "rot";
    private boolean updatingExternally;

    @ReflectionInvoked
    public OmniDirectionalTileEntityContent() {}

    public OmniDirectionalTileEntityContent(int id, int rotationMeta) {
        super(id);
        this.rotationMeta = rotationMeta;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_ROTATION, rotationMeta);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        this.rotationMeta = compound.getInteger(TAG_ROTATION);
        if (world != null) {
            updatingExternally = true;
            IBlockState state = world.getBlockState(pos);
            world.setBlockState(pos, DirectionalBlockRepresentation.Directions.ALL.toState(rotationMeta, state, true));
        }
        super.readFromNBT(compound);
    }

    public int getRotationMeta() {
        return rotationMeta;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        updateTag.setInteger(TAG_ROTATION, rotationMeta);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.rotationMeta = tag.getInteger(TAG_ROTATION);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        if (updatingExternally) {
            updatingExternally = false;
            return oldState.getBlock() != newSate.getBlock();
        } else {
            return oldState != newSate;
        }
    }
}
