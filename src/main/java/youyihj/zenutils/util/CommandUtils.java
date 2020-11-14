package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.server.IServer;
import crafttweaker.mc1120.block.MCBlockDefinition;
import crafttweaker.mc1120.item.MCItemDefinition;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.command.ZenCommand;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.CommandUtils")
@SuppressWarnings("unused")
public class CommandUtils {
    @ZenMethod
    public static IPlayer getCommandSenderAsPlayer(ZenUtilsCommandSender sender) throws PlayerNotFoundException {
        return new MCPlayer(CommandBase.getCommandSenderAsPlayer((ICommandSender) sender.getInternal()));
    }

    @ZenMethod
    public static IPlayer getPlayer(IServer server, ZenUtilsCommandSender sender, String target) throws CommandException {
        return new MCPlayer(CommandBase.getPlayer((MinecraftServer) server.getInternal(), (ICommandSender) sender.getInternal(), target));
    }

    @ZenMethod
    public static List<IPlayer> getPlayers(IServer server, ZenUtilsCommandSender sender, String target) throws CommandException {
        List<IPlayer> list = new ArrayList<>();
        list.addAll(CommandBase.getPlayers((MinecraftServer) server.getInternal(), (ICommandSender) sender.getInternal(), target).stream().map(MCPlayer::new).collect(Collectors.toList()));
        return list;
    }

    @ZenMethod
    public static IEntity getEntity(IServer server, ZenUtilsCommandSender sender, String target) throws CommandException {
        return CraftTweakerMC.getIEntity(CommandBase.getEntity((MinecraftServer) server.getInternal(), (ICommandSender) sender.getInternal(), target));
    }

    @ZenMethod
    public static List<IEntity> getEntityList(IServer server, ZenUtilsCommandSender sender, String target) throws CommandException {
        List<IEntity> list = new ArrayList<>();
        list.addAll(CommandBase.getEntityList((MinecraftServer) server.getInternal(), (ICommandSender) sender.getInternal(), target).stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList()));
        return list;
    }

    @ZenMethod
    public static IItemDefinition getItemByText(ZenUtilsCommandSender sender, String id) throws NumberInvalidException {
        return new MCItemDefinition(id, CommandBase.getItemByText((ICommandSender) sender.getInternal(), id));
    }

    @ZenMethod
    public static IBlockDefinition getBlockByText(ZenUtilsCommandSender sender, String id) throws NumberInvalidException {
        return new MCBlockDefinition(CommandBase.getBlockByText((ICommandSender) sender.getInternal(), id));
    }

    @ZenMethod
    public static void notifyWrongUsage(String message) throws WrongUsageException {
        throw new WrongUsageException(message);
    }

    @ZenMethod
    public static void notifyWrongUsage(String message, Object... replacements) throws WrongUsageException {
        throw new WrongUsageException(message, replacements);
    }

    @ZenMethod
    public static void notifyWrongUsage(ZenCommand command, ZenUtilsCommandSender sender) throws WrongUsageException {
        throw new WrongUsageException(command.getCommandUsage.getCommandUsage(sender));
    }
}
