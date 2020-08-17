package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import net.minecraftforge.server.command.CommandTreeBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import javax.annotation.Nonnull;

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

    private String name;

    @ZenProperty
    public int requiredPermissionLevel = 0;

    @ZenProperty
    public IGetCommandUsage getCommandUsage = (sender -> "commands.undefined.usage");

    @Override
    @Nonnull
    @ZenGetter("name")
    public String getName() {
        return this.name;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return this.requiredPermissionLevel;
    }
}
