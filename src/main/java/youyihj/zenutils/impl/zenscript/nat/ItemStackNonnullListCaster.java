package youyihj.zenutils.impl.zenscript.nat;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

/**
 * @author youyihj
 */
public class ItemStackNonnullListCaster {
    @ZenRegister
    @ZenExpansion("native.net.minecraft.item.ItemStack[]")
    public static class Array {
        @ZenCaster
        public static NonNullList<ItemStack> cast(ItemStack[] array) {
            return NonNullList.from(ItemStack.EMPTY, array);
        }
    }

    @ZenRegister
    @ZenExpansion("[native.net.minecraft.item.ItemStack]")
    public static class List {
        @ZenCaster
        public static NonNullList<ItemStack> cast(java.util.List<ItemStack> list) {
            return new NonNullList<ItemStack>(list, ItemStack.EMPTY) {};
        }
    }
}
