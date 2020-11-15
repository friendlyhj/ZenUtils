package youyihj.zenutils.cotx.tile;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;
import youyihj.zenutils.cotx.INBTSerializable;
import youyihj.zenutils.util.InternalUtils;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.cotx.TileData")
@ModOnly("contenttweaker")
public class TileData implements INBTSerializable {
    private final NBTTagCompound nbtTagCompound = new NBTTagCompound();

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.nbtTagCompound.merge(nbt);
    }

    @Override
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
}
