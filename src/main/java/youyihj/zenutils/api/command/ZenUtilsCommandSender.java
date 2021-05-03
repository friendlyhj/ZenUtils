package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.command.ZenUtilsCommandSender")
public class ZenUtilsCommandSender implements crafttweaker.api.command.ICommandSender{
    private final ICommandSender commandSender;

    public ZenUtilsCommandSender(ICommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public String getDisplayName() {
        return this.commandSender.getDisplayName().getFormattedText();
    }

    @Override
    public IBlockPos getPosition() {
        return new MCBlockPos(this.commandSender.getPosition());
    }

    @Override
    public IWorld getWorld() {
        return new MCWorld(this.commandSender.getEntityWorld());
    }

    @Override
    public IServer getServer() {
        return new MCServer(this.commandSender.getServer());
    }

    @Override
    public void sendMessage(String text) {
        this.commandSender.sendMessage(new TextComponentString(text));
    }

    @Override
    public Object getInternal() {
        return this.commandSender;
    }
}
