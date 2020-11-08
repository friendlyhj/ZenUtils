package youyihj.zenutils.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsPlayer")
@ZenExpansion("crafttweaker.player.IPlayer")
@SuppressWarnings("unused")
public class ZenUtilsPlayer {
    @ZenMethod
    public static boolean replaceItemInInventory(IPlayer iPlayer, int inventorySlot, IItemStack itemStack) {
        return CraftTweakerMC.getPlayer(iPlayer).replaceItemInInventory(inventorySlot, CraftTweakerMC.getItemStack(itemStack));
    }
}
