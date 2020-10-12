package youyihj.zenutils.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IGetEntityLifeSpan")
@ModOnly("contenttweaker")
public interface IGetEntityLifeSpan {
    int get(IItemStack iItemStack, IWorld world);
}
