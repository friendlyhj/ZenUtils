package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.world.MCBlockPos;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@ZenRegister
@ZenClass("mods.zenutils.command.ZenCommand")
@SuppressWarnings("unused")
public class ZenCommand extends CommandBase {
    private ZenCommand(@Nonnull String name) {
        this.name = name;
    }
    private String name;

    @ZenMethod
    public static ZenCommand create(@Nonnull String name) {
        return new ZenCommand(name);
    }

    @ZenProperty
    public ICommandExecute execute = ((command, server, sender, args) -> {});

    @ZenProperty
    public IGetCommandUsage getCommandUsage = (sender -> "commands.undefined.usage");

    @ZenProperty
    public IGetTabCompletion[] tabCompletionGetters = {};

    @ZenProperty
    public int requiredPermissionLevel = 4;

    @Override
    @ZenGetter("name")
    @Nonnull
    public String getName() {
        return this.name;
    }

    @Override
    @Nonnull
    @ZenMethod
    public String getUsage(ICommandSender sender) {
        return this.getCommandUsage.getCommandUsage(new ZenUtilsCommandSender(sender));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        this.execute.execute(this, new MCServer(server), new ZenUtilsCommandSender(sender), args);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return this.requiredPermissionLevel;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        int index = args.length - 1;
        if (this.tabCompletionGetters.length == 0 || index >= this.tabCompletionGetters.length || index < 0) return Collections.emptyList();
        return getListOfStringsMatchingLastWord(args, this.tabCompletionGetters[index]
                .get(new MCServer(server), new ZenUtilsCommandSender(sender), args, new MCBlockPos(targetPos)));
    }

    @ZenMethod
    public void register() {
        ZenCommandRegistrar.zenCommandMap.put(this.name, this);
    }
}
