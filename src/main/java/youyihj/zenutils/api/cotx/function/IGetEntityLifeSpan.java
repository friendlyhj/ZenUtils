package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenClass("mods.zenutils.cotx.IGetEntityLifeSpan")
public interface IGetEntityLifeSpan {
    int get(IItemStack iItemStack, IWorld world);
}
