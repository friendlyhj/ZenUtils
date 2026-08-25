package youyihj.zenutils.impl.network;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.network.IByteBufWriter;
import youyihj.zenutils.impl.util.LogMTErrorRunnableWrapper;

/**
 * @author youyihj
 */
public abstract class ZenUtilsMessage implements IMessage {
    protected int key = 0;
    private IByteBuf byteBuf;
    private IByteBufWriter byteBufWriter;

    public void setKey(String key) {
        this.key = key.hashCode();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = buf.readInt();
        int packetContentLength = buf.readInt();
        byte[] data = new byte[packetContentLength];

        for (int i = 0; i < packetContentLength; i++) {
            data[i] = buf.readByte();
        }

        this.byteBuf = new HeapReadOnlyByteBuf(data);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(key);
        HeapWriteOnlyByteBuf heapWriteOnlyByteBuf = new HeapWriteOnlyByteBuf();
        this.byteBuf = heapWriteOnlyByteBuf;
        getByteBufWriter().write(heapWriteOnlyByteBuf);
        int packetContentLength = heapWriteOnlyByteBuf.writeIndex();
        buf.writeInt(packetContentLength);
        buf.writeBytes(heapWriteOnlyByteBuf.getData(), 0, packetContentLength);
    }

    public final IByteBufWriter getByteBufWriter() {
        return byteBufWriter;
    }

    public final void setByteBufWriter(IByteBufWriter byteBufWriter) {
        this.byteBufWriter = byteBufWriter;
    }

    public IByteBuf getByteBuf() {
        return byteBuf;
    }

    public static class Server2Client extends ZenUtilsMessage implements IMessageHandler<Server2Client, IMessage> {
        @Override
        public IMessage onMessage(Server2Client message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(LogMTErrorRunnableWrapper.create(() -> {
                ZenUtilsNetworkHandler.INSTANCE.getClientMessageHandler(message.key).handle(CraftTweakerAPI.client.getPlayer(), message.getByteBuf());
            }));
            return null;
        }
    }

    public static class Client2Server extends ZenUtilsMessage implements IMessageHandler<Client2Server, IMessage> {
        @Override
        public IMessage onMessage(Client2Server message, MessageContext ctx) {
            CraftTweaker.server.addScheduledTask(LogMTErrorRunnableWrapper.create(() -> {
                ZenUtilsNetworkHandler.INSTANCE.getServerMessageHandler(message.key).handle(CraftTweakerAPI.server, message.getByteBuf(), CraftTweakerMC.getIPlayer(ctx.getServerHandler().player));
            }));
            return null;
        }
    }
}
