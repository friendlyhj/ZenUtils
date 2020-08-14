package youyihj.zenutils.util.object;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.entity.MCEntity;
import crafttweaker.mc1120.player.MCPlayer;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.command.ZenUtilsCommandSender")
public class ZenUtilsCommandSender implements crafttweaker.api.command.ICommandSender{
    private ICommandSender commandSender;

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

    @ZenMethod
    public boolean isServer() {
        return this.commandSender instanceof MinecraftServer;
    }

    @ZenMethod
    public IServer castToServer() {
        if (this.isServer()) {
            return new MCServer((MinecraftServer) this.commandSender);
        } else {
            CraftTweakerAPI.logError("The command sender cannot cast to server! You should use ZenUtilsCommandSender::isServer first!", new ClassCastException());
            return null;
        }
    }

    @ZenMethod
    public boolean isEntity() {
        return this.commandSender instanceof Entity;
    }

    @ZenMethod
    public IEntity castToEntity() {
        if (this.isEntity()) {
            return new MCEntity((Entity) this.commandSender);
        } else {
            CraftTweakerAPI.logError("The command sender cannot cast to entity! You should use ZenUtilsCommandSender::isEntity first!", new ClassCastException());
            return null;
        }
    }

    @ZenMethod
    public boolean isPlayer() {
        return this.commandSender instanceof EntityPlayer;
    }

    @ZenMethod
    public IPlayer castToPlayer() {
        if (this.isPlayer()) {
            return new MCPlayer((EntityPlayer) this.commandSender);
        } else {
            CraftTweakerAPI.logError("The command sender cannot cast to player! You should use ZenUtilsCommandSender::isPlayer first!", new ClassCastException());
            return null;
        }
    }
}
