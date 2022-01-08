package youyihj.zenutils.impl.delegate;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public class NoFeedbackCommandSender implements ICommandSender {
    private final ICommandSender delegate;

    public NoFeedbackCommandSender(ICommandSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return delegate.getDisplayName();
    }

    @Override
    public void sendMessage(ITextComponent component) {
        // NO-OP
    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName) {
        return delegate.canUseCommand(permLevel, commandName);
    }

    @Override
    public BlockPos getPosition() {
        return delegate.getPosition();
    }

    @Override
    public Vec3d getPositionVector() {
        return delegate.getPositionVector();
    }

    @Override
    public World getEntityWorld() {
        return delegate.getEntityWorld();
    }

    @Nullable
    @Override
    public Entity getCommandSenderEntity() {
        return delegate.getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {
        delegate.setCommandStat(type, amount);
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return delegate.getServer();
    }
}
