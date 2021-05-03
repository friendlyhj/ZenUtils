package youyihj.zenutils.api.cotx.tile;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.cotx.TileData")
@ModOnly("contenttweaker")
public class TileData implements INBTSerializable<NBTTagCompound> {
    private final NBTTagCompound nbtTagCompound = new NBTTagCompound();

    public void readFromNBT(NBTTagCompound nbt) {
        this.nbtTagCompound.merge(nbt);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.merge(this.nbtTagCompound);
        return nbt;
    }

    @ZenGetter("data")
    public IData getData() {
        return NBTConverter.from(this.writeToNBT(new NBTTagCompound()), true);
    }

    @ZenSetter("data")
    public void setData(IData data) {
        InternalUtils.checkDataMap(data);
        this.readFromNBT((NBTTagCompound) NBTConverter.from(data));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.nbtTagCompound.merge(nbt);
    }
}
