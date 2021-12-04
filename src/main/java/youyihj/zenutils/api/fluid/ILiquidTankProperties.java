package youyihj.zenutils.api.fluid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.ICommandExecute")
public interface ILiquidTankProperties {
    @Nullable
    ILiquidStack getContents();
}
