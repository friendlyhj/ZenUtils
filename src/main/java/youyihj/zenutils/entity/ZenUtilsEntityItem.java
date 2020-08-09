package youyihj.zenutils.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.item.IItemStack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsEntityItem")
@ZenExpansion("crafttweaker.entity.IEntityItem")
@SuppressWarnings("unused")
public class ZenUtilsEntityItem {
    @ZenMethod
    public static void setItem(IEntityItem iEntityItem, IItemStack itemToSet) {
        EntityItem entityItem = (EntityItem) iEntityItem.getInternal();
        entityItem.setItem((ItemStack) itemToSet.getInternal());
    }
}
