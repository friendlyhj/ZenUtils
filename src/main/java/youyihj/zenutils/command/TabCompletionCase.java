package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

@ZenRegister
@ZenClass("mods.zenutils.command.TabCompletionCase")
public class TabCompletionCase {
    static HashMap<String, IGetTabCompletion> cases = new HashMap<>();

    @ZenMethod
    public static void add(String key, IGetTabCompletion value) {
        cases.put(key, value);
    }

    static final IGetTabCompletion EMPTY_CASE = (server, sender, args, targetPos) -> Collections.emptyList();

    static {
        add("empty", EMPTY_CASE);
        add("item", ((server, sender, args, targetPos) -> Item.REGISTRY.getKeys()));
        add("block", ((server, sender, args, targetPos) -> Block.REGISTRY.getKeys()));
        add("player", ((server, sender, args, targetPos) -> Arrays.asList(((MinecraftServer) server.getInternal()).getOnlinePlayerNames())));
        add("potion", ((server, sender, args, targetPos) -> Potion.REGISTRY.getKeys()));
        add("entity", ((server, sender, args, targetPos) -> ForgeRegistries.ENTITIES.getKeys()));
        add("enchantment", ((server, sender, args, targetPos) -> ForgeRegistries.ENCHANTMENTS.getKeys()));
        add("x", ((server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getX()))));
        add("y", ((server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getY()))));
        add("z", ((server, sender, args, targetPos) -> Collections.singletonList(String.valueOf(sender.getPosition().getZ()))));
    }
}
