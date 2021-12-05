package youyihj.zenutils.api.liquid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author various.authors
 */
@ZenClass("mods.zenutils.LiquidHandler")
@ZenRegister
public class CrTLiquidHandler {
    private final IFluidHandler fluidHandler;

    private CrTLiquidHandler(IFluidHandler itemHandler) {
        Objects.requireNonNull(itemHandler);
        this.fluidHandler = itemHandler;
    }

    public static CrTLiquidHandler of(IFluidHandler fluidHandler) {
        return fluidHandler == null ? null : new CrTLiquidHandler(fluidHandler);
    }

    @ZenGetter
    public List<ILiquidTankProperties> tankProperties() {
        return this.getLiquidTankProperties(fluidHandler.getTankProperties());
    }

    private List<ILiquidTankProperties> getLiquidTankProperties(IFluidTankProperties[] fluidTankProperties) {
        return Arrays.stream(fluidTankProperties).map(MCLiquidTankProperties::new).collect(Collectors.toList());
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
