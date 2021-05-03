package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.item.CrTItemHandler;

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

    @ZenMethod
    public static CrTItemHandler getPlayerInventoryItemHandler(IPlayer player) {
        return CrTItemHandler.of(CraftTweakerMC.getPlayer(player).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
    }
}
