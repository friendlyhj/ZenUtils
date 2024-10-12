package youyihj.zenutils.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.energy.IEnergy;
import youyihj.zenutils.impl.energy.Energy;

@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public class CrTItemStackExpansion {

    @ZenGetter("energy")
    public static IEnergy getEnergy(IItemStack itemStack) {
        ItemStack mcItemStack = CraftTweakerMC.getItemStack(itemStack);
        IEnergyStorage storage = mcItemStack.getCapability(CapabilityEnergy.ENERGY, null);
        return new Energy(storage);
    }

    @ZenMethod
    public static boolean isEnergyStorage(IItemStack itemStack) {
        ItemStack mcItemStack = CraftTweakerMC.getItemStack(itemStack);
        return mcItemStack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

}
