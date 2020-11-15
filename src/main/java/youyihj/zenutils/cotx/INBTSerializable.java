package youyihj.zenutils.cotx;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author youyihj
 */
public interface INBTSerializable extends net.minecraftforge.common.util.INBTSerializable<NBTTagCompound> {
    void readFromNBT(NBTTagCompound nbt);

    NBTTagCompound writeToNBT(NBTTagCompound nbt);

    @Override
    default NBTTagCompound serializeNBT() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    default void deserializeNBT(NBTTagCompound nbt) {
        readFromNBT(nbt);
    }
}
