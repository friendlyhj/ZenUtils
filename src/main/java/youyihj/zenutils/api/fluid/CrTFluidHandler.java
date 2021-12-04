package youyihj.zenutils.api.fluid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.capability.IFluidHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Objects;

/**
 * @author various.authors
 */
@ZenClass("mods.zenutils.FluidHandler")
@ZenRegister
public class CrTFluidHandler {
    private final IFluidHandler fluidHandler;

    private CrTFluidHandler(IFluidHandler itemHandler) {
        Objects.requireNonNull(itemHandler);
        this.fluidHandler = itemHandler;
    }

    public static CrTFluidHandler of(IFluidHandler fluidHandler) {
        return fluidHandler == null ? null : new CrTFluidHandler(fluidHandler);
    }

    @ZenMethod
    public ILiquidStack drain(int maxDrain, boolean doDrain) {
        return CraftTweakerMC.getILiquidStack(fluidHandler.drain(maxDrain, doDrain));
    }

    @ZenMethod
    public ILiquidStack drain(ILiquidStack resource, boolean doDrain) {
        return CraftTweakerMC.getILiquidStack(fluidHandler.drain(CraftTweakerMC.getLiquidStack(resource), doDrain));
    }

    @ZenMethod
    public int fill(ILiquidStack resource, boolean doFill) {
        return fluidHandler.fill(CraftTweakerMC.getLiquidStack(resource), doFill);
    }
}
