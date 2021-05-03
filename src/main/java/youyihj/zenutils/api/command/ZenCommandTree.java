package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import javax.annotation.Nonnull;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.ZenCommandTree")
@SuppressWarnings("unused")
public class ZenCommandTree extends CommandTreeBase implements IZenCommand {
    private ZenCommandTree(@Nonnull String name, ZenCommand... commands) {
        this.name = name;
        for (ZenCommand command : commands) {
            this.addSubcommand(command);
        }
    }

    @ZenMethod
    public static ZenCommandTree create(@Nonnull String name, ZenCommand... commands) {
        return new ZenCommandTree(name, commands);
    }

    private final String name;

    @ZenProperty
    public int requiredPermissionLevel = 0;

    @ZenProperty
    public IGetCommandUsage getCommandUsage = IGetCommandUsage.UNDEFINED;

    @Override
    @Nonnull
    @ZenGetter("name")
    public String getName() {
        return this.name;
    }

    @Override
    @ZenMethod
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return this.getCommandUsage.getCommandUsage(new ZenUtilsCommandSender(sender));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return this.requiredPermissionLevel;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (this.getRequiredPermissionLevel() == 0) {
            return true;
        }
        return super.checkPermission(server, sender);
    }
}
