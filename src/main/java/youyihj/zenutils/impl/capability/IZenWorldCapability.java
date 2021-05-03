package youyihj.zenutils.impl.capability;

import crafttweaker.api.data.IData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author youyihj
 */
public interface IZenWorldCapability extends INBTSerializable<NBTTagCompound> {

    IData getData();

    void setData(IData data);

    void updateData(IData data);
}
