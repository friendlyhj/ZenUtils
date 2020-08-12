package youyihj.zenutils.item;

import com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack.IMutableItemStack;
import com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack.MCMutableItemStack;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

import java.lang.reflect.Field;

@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.ZenUtilsMutableItemStack")
@ZenExpansion("mods.contenttweaker.MutableItemStack")
@SuppressWarnings("unused")
public class ZenUtilsMutableItemStack {
    private static ItemStack getItemStack(IMutableItemStack iMutableItemStack) {
        MCMutableItemStack mutableItemStack = (MCMutableItemStack) iMutableItemStack;
        Class<?> clazz = mutableItemStack.getClass();
        try {
            Field stackField = clazz.getDeclaredField("itemStack");
            stackField.setAccessible(true);
            return (ItemStack) stackField.get(mutableItemStack);
        } catch (Exception e) {
            CraftTweakerAPI.logError("fail to reflect MCMutableItemStack::itemStack!", e);
            return null;
        }
    }

    @ZenMethod
    public static void setTag(IMutableItemStack iMutableItemStack, IData iData) {
        NBTBase nbtBase = NBTConverter.from(iData);
        ItemStack itemStack = getItemStack(iMutableItemStack);
        if (itemStack == null) return;
        if (nbtBase instanceof NBTTagCompound) {
            itemStack.setTagCompound((NBTTagCompound) nbtBase);
        } else {
            CraftTweakerAPI.logError(iData.getClass().getName() + " cannot cast to the NBT of item stack!", new IllegalArgumentException());
        }
    }

    @ZenMethod
    public static void setEmptyTag(IMutableItemStack iMutableItemStack) {
        ItemStack itemStack = getItemStack(iMutableItemStack);
        if (itemStack != null) {
            itemStack.setTagCompound(null);
        }
    }
}
