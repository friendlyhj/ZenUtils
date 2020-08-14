package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.server.MCServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.util.object.ZenUtilsCommandSender;

import javax.annotation.Nonnull;

@ZenRegister
@ZenClass("mods.zenutils.command.ZenCommand")
@SuppressWarnings("unused")
public class ZenCommand extends CommandBase {
    public ZenCommand(@Nonnull String name) {
        this.name = name;
    }
    private String name;

    @ZenMethod
    public static ZenCommand create(@Nonnull String name) {
        return new ZenCommand(name);
    }

    @ZenProperty
    public ICommandExecute execute = ((server, sender, args) -> {});

    @ZenProperty
    public IGetCommandUsage getCommandUsage = (sender -> "don't define command usage!");

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
    public String getUsage(ICommandSender sender) {
        return this.getCommandUsage.getCommandUsage(new ZenUtilsCommandSender(sender));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        this.execute.execute(new MCServer(server), new ZenUtilsCommandSender(sender), args);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return this.requiredPermissionLevel;
    }

    @ZenMethod
    public void register() {
        ZenCommandRegistrar.zenCommandMap.put(this.name, this);
    }
}
