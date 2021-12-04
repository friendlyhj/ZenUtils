package youyihj.zenutils.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import stanhebben.zenscript.annotations.IterableSimple;
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
    public ILiquidStack drain(FluidStack resource, boolean doDrain) {
        return CraftTweakerMC.getILiquidStack(fluidHandler.drain(resource, doDrain));
    }

    @ZenMethod
    public int fill(FluidStack resource, boolean doFill) {
        return fluidHandler.fill(resource, doFill);
    }
}
