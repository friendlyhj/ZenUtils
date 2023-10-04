package youyihj.zenutils.impl.player;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import youyihj.zenutils.api.player.IActionResult;

/**
 * @author youyihj
 */
public class ActionResult<T> implements IActionResult<T> {
    private final ItemStack item;
    private final T result;

    public ActionResult(ItemStack item, T result) {
        this.item = item;
        this.result = result;
    }

    @Override
    public IItemStack getItem() {
        return CraftTweakerMC.getIItemStack(item);
    }

    @Override
    public T getResult() {
        return result;
    }
}
