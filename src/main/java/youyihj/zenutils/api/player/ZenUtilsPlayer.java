package youyihj.zenutils.api.player;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.api.item.CrTItemHandler;
import youyihj.zenutils.impl.player.PlayerInteractionSimulation;

/**
 * @author youyihj
 */
@ZenRegister
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

    @ZenMethod
    @ZenGetter("xpPoints")
    public static int getXpPoints(IPlayer player) {
        return CraftTweakerMC.getPlayer(player).experienceTotal;
    }

    @ZenMethod
    @ZenSetter("xpPoints")
    public static void setXpPoints(IPlayer player, int xpPoints) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        mcPlayer.addExperienceLevel(-mcPlayer.experienceLevel-1);
        mcPlayer.addExperience(xpPoints);
    }

    @ZenMethod
    public static IActionResult<EnumActionResult> simulateRightClickItem(IPlayer player, IItemStack stack, @Optional IEntityEquipmentSlot hand) {
        return PlayerInteractionSimulation.simulateRightClickItem(player, stack, hand);
    }

    @ZenMethod
    public static IActionResult<EnumActionResult> simulateRightClickBlock(IPlayer player, IItemStack stack, @Optional IEntityEquipmentSlot hand, @Optional IBlockPos pos, @Optional IFacing facing, @Optional(valueDouble = -1) float hitX, @Optional(valueDouble = -1) float hitY, @Optional(valueDouble = -1) float hitZ) {
        return PlayerInteractionSimulation.simulateRightClickBlock(player, stack, hand, pos, facing, hitX, hitY, hitZ);
    }

    @ZenMethod
    public static IActionResult<EnumActionResult> simulateRightClickEntity(IPlayer player, IEntity entity, @Optional IItemStack stack, @Optional IEntityEquipmentSlot hand) {
        return PlayerInteractionSimulation.simulateRightClickEntity(player, entity, stack, hand);
    }

    @ZenMethod
    public static IActionResult<Void> simulateLeftClickBlock(IPlayer player, @Optional IItemStack stack, @Optional IBlockPos pos, @Optional IFacing side) {
        return PlayerInteractionSimulation.simulateLeftClickBlock(player, stack, pos, side);
    }

    @ZenMethod
    public static IItemStack simulateUseItemFinish(IPlayer player, IItemStack itemStack, @Optional IEntityEquipmentSlot hand) {
        return PlayerInteractionSimulation.simulateUseItemFinish(player, itemStack, hand);
    }
}
