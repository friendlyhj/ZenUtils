package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.util.object.StringList;

@ZenExpansion("mods.zenutils.command.IGetTabCompletion")
@ZenRegister
@SuppressWarnings("unused")
public class DefaultGetTabCompletion {
    @ZenMethodStatic
    public static IGetTabCompletion item() {
        return (server, sender, targetPos) -> StringList.create(Item.REGISTRY.getKeys());
    }

    @ZenMethodStatic
    public static IGetTabCompletion block() {
        return (server, sender, targetPos) -> StringList.create(Block.REGISTRY.getKeys());
    }

    @ZenMethodStatic
    public static IGetTabCompletion player() {
        return (server, sender, targetPos) -> StringList.create(CraftTweakerMC.getMCServer(server).getOnlinePlayerNames());
    }

    @ZenMethodStatic
    public static IGetTabCompletion potion() {
        return (server, sender, targetPos) -> StringList.create(Potion.REGISTRY.getKeys());
    }

    @ZenMethodStatic
    public static IGetTabCompletion x() {
        return ((server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getX())));
    }

    @ZenMethodStatic
    public static IGetTabCompletion y() {
        return ((server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getY())));
    }

    @ZenMethodStatic
    public static IGetTabCompletion z() {
        return ((server, sender, targetPos) -> (targetPos == null) ? StringList.empty() : StringList.singletonList(String.valueOf(targetPos.getZ())));
    }
}
