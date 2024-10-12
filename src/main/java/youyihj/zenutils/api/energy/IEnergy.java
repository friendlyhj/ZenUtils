package youyihj.zenutils.api.energy;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.zenutils.energy.IEnergy")
@ZenRegister
public interface IEnergy {

    Object getInternal();

    @ZenMethod
    int receiveEnergy(int maxReceive, @Optional boolean simulate);

    @ZenMethod
    int extractEnergy(int maxExtract, @Optional boolean simulate);

    @ZenMethod
    int getEnergyStored();

    @ZenMethod
    int getMaxEnergyStored();

    @ZenMethod
    boolean canExtract();

    @ZenMethod
    boolean canReceive();
}
