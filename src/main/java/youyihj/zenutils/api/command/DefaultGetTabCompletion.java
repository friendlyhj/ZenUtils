package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.api.util.StringList;

import java.util.Arrays;

/**
 * @author youyihj
 */
@ZenExpansion("mods.zenutils.command.IGetTabCompletion")
@ZenRegister
@SuppressWarnings("unused")
public class DefaultGetTabCompletion {

    private static final IGetTabCompletion ITEM = (server, sender, targetPos) -> StringList.create(Item.REGISTRY.getKeys());
    private static final IGetTabCompletion BLOCK  = (server, sender, targetPos) -> StringList.create(Block.REGISTRY.getKeys());
    private static final IGetTabCompletion PLAYER = (server, sender, targetPos) -> StringList.create(CraftTweakerMC.getMCServer(server).getOnlinePlayerNames());
    private static final IGetTabCompletion POTION = (server, sender, targetPos) -> StringList.create(Potion.REGISTRY.getKeys());
    private static final IGetTabCompletion X = (server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getX()));
    private static final IGetTabCompletion Y = (server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getY()));
    private static final IGetTabCompletion Z = (server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getZ()));

    private static final IGetTabCompletion BOOLEAN = (server, sender, targetPos) -> StringList.create(Arrays.asList("true", "false"));

    private static final IGetTabCompletion EMPTY = ((server, sender, targetPos) -> StringList.empty());

    @ZenMethodStatic
    public static IGetTabCompletion item() {
        return ITEM;
    }

    @ZenMethodStatic
    public static IGetTabCompletion block() {
        return BLOCK;
    }

    @ZenMethodStatic
    public static IGetTabCompletion player() {
        return PLAYER;
    }

    @ZenMethodStatic
    public static IGetTabCompletion potion() {
        return POTION;
    }

    @ZenMethodStatic
    public static IGetTabCompletion x() {
        return X;
    }

    @ZenMethodStatic
    public static IGetTabCompletion y() {
        return Y;
    }

    @ZenMethodStatic
    public static IGetTabCompletion z() {
        return Z;
    }

    @ZenMethodStatic("boolean")
    public static IGetTabCompletion bool() {
        return BOOLEAN;
    }

    @ZenMethodStatic
    public static IGetTabCompletion empty() {
        return EMPTY;
    }
}
