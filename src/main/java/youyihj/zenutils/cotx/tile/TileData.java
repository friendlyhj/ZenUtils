package youyihj.zenutils.cotx.tile;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;
import youyihj.zenutils.cotx.INBTSerializable;

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
        if (!(data instanceof DataMap)) {
            CraftTweakerAPI.logError("data argument must be DataMap", new IllegalArgumentException());
        }
        this.readFromNBT((NBTTagCompound) NBTConverter.from(data));
    }
}
