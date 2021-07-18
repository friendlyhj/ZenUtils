package youyihj.zenutils.impl.network;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.player.MCPlayer;
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
    public final void fromBytes(ByteBuf buf) {
        this.key = buf.readInt();
        this.byteBuf = new ZenUtilsByteBuf(buf.copy());
        readExtraBytes(byteBuf);
    }

    @Override
    public final void toBytes(ByteBuf buf) {
        this.byteBuf = new ZenUtilsByteBuf(buf);
        this.byteBuf.writeInt(key);
        this.writeExtraBytes(this.byteBuf);
    }

    public final IByteBufWriter getByteBufWriter() {
        return byteBufWriter;
    }

    public final void setByteBufWriter(IByteBufWriter byteBufWriter) {
        this.byteBufWriter = byteBufWriter;
    }

    protected abstract void writeExtraBytes(IByteBuf buf);

    protected abstract void readExtraBytes(IByteBuf buf);

    public IByteBuf getByteBuf() {
        return byteBuf;
    }

    public static class Server2Client extends ZenUtilsMessage implements IMessageHandler<Server2Client, IMessage> {

        @Override
        protected void writeExtraBytes(IByteBuf buf) {
            getByteBufWriter().write(buf);
        }

        @Override
        protected void readExtraBytes(IByteBuf buf) {
            // NO-OP
        }

        @Override
        public IMessage onMessage(Server2Client message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(LogMTErrorRunnableWrapper.create(() ->
                    ZenUtilsNetworkHandler.INSTANCE.getClientMessageHandler(message.key).handle(CraftTweakerAPI.client.getPlayer(), message.getByteBuf())
            ));
            return null;
        }
    }

    public static class Client2Server extends ZenUtilsMessage implements IMessageHandler<Client2Server, IMessage> {

        @Override
        protected void readExtraBytes(IByteBuf buf) {
            // NO-OP
        }

        @Override
        protected void writeExtraBytes(IByteBuf buf) {
            getByteBufWriter().write(buf);
        }

        @Override
        public IMessage onMessage(Client2Server message, MessageContext ctx) {
            IByteBuf byteBuf = message.getByteBuf();
            CraftTweaker.server.addScheduledTask(LogMTErrorRunnableWrapper.create(() ->
                    ZenUtilsNetworkHandler.INSTANCE.getServerMessageHandler(message.key).handle(CraftTweakerAPI.server, byteBuf, new MCPlayer(ctx.getServerHandler().player))
            ));
            return null;
        }
    }
}
