package youyihj.zenutils.cotx;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author youyihj
 */
public interface INBTSerializable {
    void readFromNBT(NBTTagCompound nbt);

    NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
