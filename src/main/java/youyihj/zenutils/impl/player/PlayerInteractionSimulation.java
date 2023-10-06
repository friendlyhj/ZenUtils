package youyihj.zenutils.impl.player;

import com.google.common.base.Throwables;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import youyihj.zenutils.api.network.ZenNetworkHandler;
import youyihj.zenutils.api.player.IActionResult;

import java.util.concurrent.Callable;

/**
 * @author youyihj
 */
public class PlayerInteractionSimulation {
    private static final String INTERACT_ENTITY_MESSAGE_KEY = "zenutils.internal.interactEntity";

    public static IActionResult<EnumActionResult> simulateRightClickItem(IPlayer player, IItemStack stack, IEntityEquipmentSlot hand) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        EnumHand mcHand = hand == null ? EnumHand.MAIN_HAND : CraftTweakerMC.getHand(hand);
        ItemStack mcItem = CraftTweakerMC.getItemStack(stack);
        if (mcPlayer instanceof EntityPlayerMP) {
            return ghostItemOperation(mcPlayer, mcItem, mcHand, () -> {
                PlayerInteractionManager interactionManager = ((EntityPlayerMP) mcPlayer).interactionManager;
                return interactionManager.processRightClick(mcPlayer, mcPlayer.world, mcItem, mcHand);
            });
        }
        return new ActionResult<>(mcItem, EnumActionResult.PASS);
    }

    public static IActionResult<EnumActionResult> simulateRightClickBlock(IPlayer player, IItemStack stack, IEntityEquipmentSlot hand, IBlockPos pos, IFacing facing, float hitX, float hitY, float hitZ) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        InteractBlockContext context = new InteractBlockContext(mcPlayer, CraftTweakerMC.getBlockPos(pos), CraftTweakerMC.getFacing(facing), hitX, hitY, hitZ);
        ItemStack mcItem = CraftTweakerMC.getItemStack(stack);
        if (mcPlayer instanceof EntityPlayerMP) {
            EnumHand mcHand = hand == null ? EnumHand.MAIN_HAND : CraftTweakerMC.getHand(hand);
            return ghostItemOperation(mcPlayer, mcItem, mcHand, () -> {
                PlayerInteractionManager interactionManager = ((EntityPlayerMP) mcPlayer).interactionManager;
                return interactionManager.processRightClickBlock(mcPlayer, mcPlayer.world, mcItem, mcHand, context.getPos(), context.getSide(), context.getHitX(), context.getHitY(), context.getHitZ());
            });
        }
        return new ActionResult<>(mcItem, EnumActionResult.PASS);
    }

    public static IActionResult<EnumActionResult> simulateRightClickEntity(IPlayer player, IEntity entity, IItemStack stack, IEntityEquipmentSlot hand) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        Entity mcEntity = CraftTweakerMC.getEntity(entity);
        EnumHand mcHand = hand == null ? EnumHand.MAIN_HAND : CraftTweakerMC.getHand(hand);
        ZenNetworkHandler.sendTo(INTERACT_ENTITY_MESSAGE_KEY, player, byteBuf -> {
            byteBuf.writeInt(mcEntity.getEntityId());
            byteBuf.writeItemStack(stack);
            byteBuf.writeBoolean(mcHand == EnumHand.MAIN_HAND);
        });
        return ghostItemOperation(mcPlayer, CraftTweakerMC.getItemStack(stack), mcHand, () -> mcPlayer.interactOn(mcEntity, mcHand));
    }

    public static IActionResult<Void> simulateLeftClickBlock(IPlayer player, IItemStack stack, IBlockPos pos, IFacing side) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        InteractBlockContext context = new InteractBlockContext(mcPlayer, CraftTweakerMC.getBlockPos(pos), CraftTweakerMC.getFacing(side));
        if (mcPlayer instanceof EntityPlayerMP) {
            return ghostItemOperation(mcPlayer, CraftTweakerMC.getItemStack(stack), EnumHand.MAIN_HAND, () -> {
                PlayerInteractionManager interactionManager = ((EntityPlayerMP) mcPlayer).interactionManager;
                interactionManager.onBlockClicked(context.getPos(), context.getSide());
                return null;
            });
        }
        return new ActionResult<>(ItemStack.EMPTY, null);
    }

    public static IItemStack simulateUseItemFinish(IPlayer player, IItemStack itemStack, IEntityEquipmentSlot hand) {
        ItemStack mcItem = CraftTweakerMC.getItemStack(itemStack);
        EnumHand mcHand = CraftTweakerMC.getHand(hand);
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        if (mcHand == null) {
            mcHand = mcPlayer.getActiveHand();
        }
        return ghostItemOperation(mcPlayer, mcItem, mcHand, () -> {
            ItemStack result = mcItem.onItemUseFinish(CraftTweakerMC.getWorld(player.getWorld()), mcPlayer);
            result = ForgeEventFactory.onItemUseFinish(mcPlayer, mcItem.copy(), mcItem.getMaxItemUseDuration(), result);
            return CraftTweakerMC.getIItemStack(result);
        }).getResult();
    }

    public static void registerNetworkMessage() {
        ZenNetworkHandler.registerServer2ClientMessage(INTERACT_ENTITY_MESSAGE_KEY, (player, byteBuf) -> {
            World world = CraftTweakerMC.getWorld(player.getWorld());
            Entity entity = world.getEntityByID(byteBuf.readInt());
            IItemStack itemStack = byteBuf.readItemStack();
            if (entity != null) {
                EnumHand hand = byteBuf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
                ghostItemOperation(mcPlayer, CraftTweakerMC.getItemStack(itemStack), hand, () -> mcPlayer.interactOn(entity, hand));
            }
        });
    }

    private static <T> IActionResult<T> ghostItemOperation(EntityPlayer player, ItemStack item, EnumHand hand, Callable<T> operation) {
        ItemStack origin = player.getHeldItem(hand);
        player.setHeldItem(hand, item);
        try {
            T result = operation.call();
            return new ActionResult<>(player.getHeldItem(hand), result);
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        } finally {
            player.setHeldItem(hand, origin);
        }
    }
}
