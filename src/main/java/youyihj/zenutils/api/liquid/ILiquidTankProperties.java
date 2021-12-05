package youyihj.zenutils.api.liquid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

/**
 * @author various.authors
 */
@ZenRegister
@ZenClass("mods.zenutils.liquid.ILiquidTankProperties")
public interface ILiquidTankProperties {

    /**
     * @return A copy of the fluid contents of this tank. May be null.
     */
    @ZenGetter("contents")
    @Nullable
    ILiquidStack getContents();

    /**
     * @return The maximum amount of fluid this tank can hold, in millibuckets.
     */
    @ZenGetter("capacity")
    int getCapacity();

    /**
     * Returns true if the tank can be filled at any time (even if it is currently full).
     * It does not consider the contents or capacity of the tank.
     *
     * This value is constant. If the tank behavior is more complicated, returns true.
     */
    @ZenGetter("canFill")
    boolean canFill();

    /**
     * Returns true if the tank can be drained at any time (even if it is currently empty).
     * It does not consider the contents or capacity of the tank.
     *
     * This value is constant. If the tank behavior is more complicated, returns true.
     */
    @ZenGetter("canDrain")
    boolean canDrain();

    /**
     * Returns true if the tank can be filled with a specific type of fluid.
     * Used as a filter for fluid types.
     *
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever fill with this type of fluid.
     * {@link ILiquidStack} is used here because fluid properties can depend on NBT, the amount is ignored.
     */
    @ZenMethod("canFillFluidType")
    boolean canFillFluidType(ILiquidStack liquidStack);

    /**
     * Returns true if the tank can drain out this a specific of fluid.
     * Used as a filter for fluid types.
     *
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever drain out this type of fluid.
     * {@link ILiquidStack} is used here because fluid properties can depend on NBT, the amount is ignored.
     */
    @ZenMethod("canDrainFluidType")
    boolean canDrainFluidType(ILiquidStack liquidStack);
}
