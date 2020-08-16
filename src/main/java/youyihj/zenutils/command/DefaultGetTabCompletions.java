package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;

@ZenRegister
@ZenClass("mods.zenutils.command.DefaultGetTabCompletions")
@SuppressWarnings("unused")
public class DefaultGetTabCompletions {
    private static final IGetTabCompletion EMPTY = (server, sender, args, targetPos) -> Collections.emptyList();
    private static final IGetTabCompletion ITEM = (server, sender, args, targetPos) -> Item.REGISTRY.getKeys();
    private static final IGetTabCompletion BLOCK = (server, sender, args, targetPos) -> Block.REGISTRY.getKeys();
    private static final IGetTabCompletion PLAYER = (server, sender, args, targetPos) -> Arrays.asList(((MinecraftServer) server.getInternal()).getOnlinePlayerNames());
    private static final IGetTabCompletion ENTITY = (server, sender, args, targetPos) -> ForgeRegistries.ENTITIES.getKeys();
    private static final IGetTabCompletion ENCHANTMENT = (server, sender, args, targetPos) -> ForgeRegistries.ENCHANTMENTS.getKeys();
    private static final IGetTabCompletion X = (server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getX()));
    private static final IGetTabCompletion Y = (server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getY()));
    private static final IGetTabCompletion Z = (server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getZ()));

    @ZenMethod
    public static IGetTabCompletion getEmpty() {
        return EMPTY;
    }

    @ZenMethod
    public static IGetTabCompletion getItem() {
        return ITEM;
    }

    @ZenMethod
    public static IGetTabCompletion getBlock() {
        return BLOCK;
    }

    @ZenMethod
    public static IGetTabCompletion getPlayer() {
        return PLAYER;
    }

    @ZenMethod
    public static IGetTabCompletion getEntity() {
        return ENTITY;
    }

    @ZenMethod
    public static IGetTabCompletion getEnchantment() {
        return ENCHANTMENT;
    }

    @ZenMethod
    public static IGetTabCompletion getX() {
        return X;
    }

    @ZenMethod
    public static IGetTabCompletion getY() {
        return Y;
    }

    @ZenMethod
    public static IGetTabCompletion getZ() {
        return Z;
    }
}
