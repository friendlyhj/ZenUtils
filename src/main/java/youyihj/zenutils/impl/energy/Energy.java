package youyihj.zenutils.impl.energy;

import net.minecraftforge.energy.IEnergyStorage;
import youyihj.zenutils.api.energy.IEnergy;

public class Energy implements IEnergy {
    private final IEnergyStorage internal;

    public Energy(IEnergyStorage storage) {
        this.internal = storage;
    }

    @Override
    public Object getInternal() {
        return internal;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return internal.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return internal.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return internal.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return internal.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return internal.canExtract();
    }

    @Override
    public boolean canReceive() {
        return internal.canReceive();
    }
}
