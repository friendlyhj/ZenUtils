package youyihj.zenutils.capability;

import com.google.common.collect.Maps;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import youyihj.zenutils.util.InternalUtils;

/**
 * @author youyihj
 */
public class ZenWorldCapability implements IZenWorldCapability {
    private IData data = new DataMap(Maps.newHashMap(), false);

    @Override
    public IData getData() {
        return this.data;
    }

    @Override
    public void setData(IData data) {
        InternalUtils.checkDataMap(data);
        this.data = data;
    }

    @Override
    public void updateData(IData data) {
        this.data = this.data.update(data);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return ((NBTTagCompound) NBTConverter.from(data));
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.data = NBTConverter.from(nbt, false);
    }
}
