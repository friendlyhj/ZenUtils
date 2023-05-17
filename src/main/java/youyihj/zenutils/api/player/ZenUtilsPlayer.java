package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import stanhebben.zenscript.annotations.*;
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

    @ZenMethod
    public static int readStat(IPlayer player, PlayerStat stat) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        if (mcPlayer instanceof EntityPlayerMP) {
            return ((EntityPlayerMP) mcPlayer).getStatFile().readStat(stat.getInternal());
        }
        return 0;
    }

    @ZenMethod
    public static void addStat(IPlayer player, PlayerStat stat, @Optional(valueLong = 1) int amount) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        mcPlayer.addStat(stat.getInternal(), amount);
    }

    @ZenMethod
    public static void takeStat(IPlayer player, PlayerStat stat) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        mcPlayer.takeStat(stat.getInternal());
    }

    @ZenMethod
    @ZenGetter("fake")
    public static boolean isFake(IPlayer player) {
        return CraftTweakerMC.getPlayer(player) instanceof FakePlayer;
    }
}
