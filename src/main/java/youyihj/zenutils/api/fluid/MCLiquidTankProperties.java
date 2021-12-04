package youyihj.zenutils.api.fluid;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import stanhebben.zenscript.annotations.ZenMethod;

public class MCLiquidTankProperties implements ILiquidTankProperties {
    private ILiquidStack contents;

    public MCLiquidTankProperties(IFluidTankProperties properties) {
      this.contents = CraftTweakerMC.getILiquidStack(properties.getContents());
    }

    public ILiquidStack getContents() {
        return this.contents;
    }
}
