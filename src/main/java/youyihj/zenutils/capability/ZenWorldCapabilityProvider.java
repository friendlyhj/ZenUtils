package youyihj.zenutils.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public class ZenWorldCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
    private final IZenWorldCapability zenWorldCapability;

    public ZenWorldCapabilityProvider(IZenWorldCapability capability) {
        this.zenWorldCapability = capability;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY) {
            return ZenWorldCapabilityHandler.ZEN_WORLD_CAPABILITY.cast(this.zenWorldCapability);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.zenWorldCapability.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.zenWorldCapability.deserializeNBT(nbt);
    }
}
