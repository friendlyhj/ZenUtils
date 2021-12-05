package youyihj.zenutils.api.fluid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author various.authors
 */
@ZenClass("mods.zenutils.LiquidTankProperties")
@ZenRegister
public class MCLiquidTankProperties implements ILiquidTankProperties {
    private ILiquidStack contents;

    public MCLiquidTankProperties(IFluidTankProperties properties) {
      this.contents = CraftTweakerMC.getILiquidStack(properties.getContents());
    }

    @ZenMethod
    public ILiquidStack getContents() {
        return this.contents;
    }
}
