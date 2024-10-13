package youyihj.zenutils.api.energy;

import crafttweaker.annotations.ZenRegister;
import net.minecraftforge.energy.IEnergyStorage;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.EnergyStorage")
@ZenRegister
public class CrTEnergyStorage {
    private final IEnergyStorage storage;

    private CrTEnergyStorage(IEnergyStorage storage) {
        this.storage = storage;
    }

    public static CrTEnergyStorage of(IEnergyStorage storage) {
        return storage == null ? null : new CrTEnergyStorage(storage);
    }

    @ZenMethod
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @ZenGetter("energyStored")
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @ZenGetter
    public boolean canExtract() {
        return storage.canExtract();
    }

    @ZenMethod
    public int extractEnergy(int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @ZenGetter
    public boolean canReceive() {
        return storage.canReceive();
    }

    @ZenGetter("maxEnergyStored")
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }
}
