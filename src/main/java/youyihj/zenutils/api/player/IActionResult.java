package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IActionResult")
public interface IActionResult<T> {
    @ZenGetter("item")
    IItemStack getItem();

    T getResult();

    @ZenGetter("result")
    default String getResultString() {
        return String.valueOf(getResult());
    }
}
