package youyihj.zenutils.api.cotx.item;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import youyihj.zenutils.api.annotation.ExpandCoTEntry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author youyihj
 */
@ExpandCoTEntry
public class EnergyItemContent extends ExpandItemContent implements IEnergyContainerItem {

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private final EnergyItemRepresentation energyItemRepresentation;

    public EnergyItemContent(EnergyItemRepresentation itemRepresentation) {
        super(itemRepresentation);
        this.capacity = itemRepresentation.capacity;
        this.maxReceive = itemRepresentation.maxReceive;
        this.maxExtract = itemRepresentation.maxExtract;
        this.maxStackSize = 1;
        this.energyItemRepresentation = itemRepresentation;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyContainerItemWrapper(stack, this);
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (this.canReceive()) {
            int received =  Math.min(capacity - this.getEnergyStored(container), Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                this.setEnergyStored(container, this.getEnergyStored(container) + received);
            }
            return received;
        }
        return 0;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (this.canExtract()) {
            int extracted = Math.min(this.getEnergyStored(container), Math.min(this.maxExtract, maxExtract));
            if (!simulate) {
                this.setEnergyStored(container, this.getEnergyStored(container) - extracted);
            }
            return extracted;
        }
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.hasTagCompound() && container.getTagCompound().hasKey("energy")) {
            return container.getTagCompound().getInteger("energy");
        } else {
            this.setEnergyStored(container, 0);
            return 0;
        }
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.capacity;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void setEnergyStored(ItemStack container, int energy) {
        if (container.hasTagCompound()) {
            container.getTagCompound().setInteger("energy", energy);
        } else {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("energy", energy);
            container.setTagCompound(nbt);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.zenutils.energy", this.getEnergyStored(stack), this.getMaxEnergyStored(stack)));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - (double) this.getEnergyStored(stack) / (double) this.getMaxEnergyStored(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return this.getEnergyStored(stack) != this.getMaxEnergyStored(stack);
    }

    @ExpandCoTEntry.RepresentationGetter
    public EnergyItemRepresentation getEnergyItemRepresentation() {
        return energyItemRepresentation;
    }
}
