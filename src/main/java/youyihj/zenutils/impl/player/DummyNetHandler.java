package youyihj.zenutils.impl.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

/**
 * @author youyihj
 */
public class DummyNetHandler extends NetHandlerPlayServer {
    public DummyNetHandler(MinecraftServer server, EntityPlayerMP playerIn) {
        super(server, new NetworkManager(EnumPacketDirection.SERVERBOUND), playerIn);
    }

    @Override
    public void disconnect(ITextComponent textComponent) {
    }

    @Override
    public void processVehicleMove(CPacketVehicleMove packetIn) {
    }

    @Override
    public void sendPacket(Packet<?> packetIn) {
    }
}
