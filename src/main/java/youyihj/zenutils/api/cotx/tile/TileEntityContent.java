package youyihj.zenutils.api.cotx.tile;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.api.zenscript.SidedZenRegister;
import youyihj.zenutils.impl.util.InternalUtils;

import javax.annotation.Nonnull;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.TileEntityInGame")
public class TileEntityContent extends TileEntity implements ITickable {
    private final TileData customData = new TileData();
    private static final String TAG_ID = "TileID";
    public static final String TAG_CUSTOM_DATA = "CustomData";
    private int id;

    @ReflectionInvoked
    public TileEntityContent() {}

    public TileEntityContent(int id) {
        this.id = id;
    }

    @ZenGetter("id")
    public int getId() {
        return id;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_ID, id);
        if (!compound.hasKey(TAG_CUSTOM_DATA)) {
            compound.setTag(TAG_CUSTOM_DATA, new NBTTagCompound());
        }
        customData.writeToNBT(compound.getCompoundTag(TAG_CUSTOM_DATA));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        customData.readFromNBT(compound.getCompoundTag(TAG_CUSTOM_DATA));
        this.id = compound.getInteger(TAG_ID);
        super.readFromNBT(compound);
    }

    @ZenGetter("data")
    public IData getCustomData() {
        return customData.getData();
    }

    @ZenSetter("data")
    public void setCustomData(IData data) {
        customData.readFromNBT((NBTTagCompound) NBTConverter.from(data));
        this.markDirty();
    }

    @ZenMethod
    public void updateCustomData(IData data) {
        InternalUtils.checkDataMap(data);
        setCustomData(getCustomData().add(data));
    }

    @Override
    public void update() {
        TileEntityManager.getTickFunction(id)
                .tick(this, new MCWorld(world), new MCBlockPos(pos));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
