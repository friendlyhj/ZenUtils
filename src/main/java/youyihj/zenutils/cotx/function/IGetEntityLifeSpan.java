package youyihj.zenutils.cotx.function;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.cotx.IGetEntityLifeSpan")
@ModOnly("contenttweaker")
public interface IGetEntityLifeSpan {
    int get(IItemStack iItemStack, IWorld world);
}
