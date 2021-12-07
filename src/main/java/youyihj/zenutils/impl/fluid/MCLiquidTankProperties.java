package youyihj.zenutils.impl.fluid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.liquid.ILiquidTankProperties;

/**
 * @author various.authors
 */
public class MCLiquidTankProperties implements ILiquidTankProperties {
    private IFluidTankProperties properties;

    public MCLiquidTankProperties(IFluidTankProperties properties) {
      this.properties = properties;
    }

    public ILiquidStack getContents() {
        return CraftTweakerMC.getILiquidStack(properties.getContents());
    }

    public int getCapacity() {
        return properties.getCapacity();
    }

    public boolean canFill() {
        return properties.canFill();
    }

    public boolean canDrain() {
        return properties.canDrain();
    }

    public boolean canFillFluidType(ILiquidStack liquidStack) {
        return properties.canFillFluidType(CraftTweakerMC.getLiquidStack(liquidStack));
    }

    public boolean canDrainFluidType(ILiquidStack liquidStack) {
        return properties.canDrainFluidType(CraftTweakerMC.getLiquidStack(liquidStack));
    }
}
