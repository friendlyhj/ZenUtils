package youyihj.zenutils.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IMutableItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidUtil;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.api.energy.CrTEnergyStorage;
import youyihj.zenutils.api.liquid.CrTLiquidHandler;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.item.IMutableItemStack")
public class ZenUtilsMutableItemStack {
    @ZenGetter("fluidHandler")
    public static CrTLiquidHandler getFluidHandler(IMutableItemStack item) {
        return CrTLiquidHandler.of(FluidUtil.getFluidHandler(CraftTweakerMC.getItemStack(item)));
    }

    @ZenGetter("energyStorage")
    public static CrTEnergyStorage getEnergyStorage(IMutableItemStack item) {
        ItemStack mcItem = CraftTweakerMC.getItemStack(item);
        if (mcItem.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return CrTEnergyStorage.of(mcItem.getCapability(CapabilityEnergy.ENERGY, null));
        }
        return null;
    }
}
