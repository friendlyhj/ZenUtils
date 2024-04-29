package youyihj.zenutils.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.util.StringList;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ITooltipModifier")
@FunctionalInterface
public interface ITooltipModifier {
    void apply(IItemStack stack, StringList tooltip, boolean shiftPressed, boolean advanced);
}
