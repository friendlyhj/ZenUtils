package youyihj.zenutils.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsPlayer")
@ZenExpansion("crafttweaker.player.IPlayer")
@SuppressWarnings("unused")
public class ZenUtilsPlayer {
    @ZenMethod
    public static boolean replaceItemInInventory(IPlayer iPlayer, int inventorySlot, IItemStack itemStack) {
        return CraftTweakerMC.getPlayer(iPlayer).replaceItemInInventory(inventorySlot, (ItemStack) itemStack.getInternal());
    }
}
