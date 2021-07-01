package youyihj.zenutils.impl.network;

import crafttweaker.CraftTweakerAPI;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.network.IClientMessageHandler;
import youyihj.zenutils.api.network.IServerMessageHandler;

/**
 * @author youyihj
 */
public abstract class ZenUtilsMessage implements IMessage {
    private IByteBuf byteBuf;
    protected int key = 0;

    public void setKey(String key) {
        this.key = key.hashCode();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = buf.readInt();
        this.byteBuf = new ZenUtilsByteBuf(buf.copy());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.byteBuf = new ZenUtilsByteBuf(buf);
        this.byteBuf.writeInt(key);
        this.writeExtraBytes(this.byteBuf);
    }

    protected abstract void writeExtraBytes(IByteBuf buf);

    public IByteBuf getByteBuf() {
        return byteBuf;
    }

    public static class Server2Client extends ZenUtilsMessage implements IMessageHandler<Server2Client, IMessage> {
        private IServerMessageHandler serverMessageHandler;

        public void setServerMessageHandler(IServerMessageHandler serverMessageHandler) {
            this.serverMessageHandler = serverMessageHandler;
        }

        @Override
        protected void writeExtraBytes(IByteBuf buf) {
            serverMessageHandler.handle(CraftTweakerAPI.server, buf);
        }

        @Override
        public IMessage onMessage(Server2Client message, MessageContext ctx) {
            try {
                ZenUtilsNetworkHandler.INSTANCE.getClientMessageHandler(message.key).handle(CraftTweakerAPI.client.getPlayer(), message.getByteBuf());
            } catch (Exception e) {
                CraftTweakerAPI.logError(null, e);
            }
            return null;
        }
    }

    public static class Client2Server extends ZenUtilsMessage implements IMessageHandler<Client2Server, IMessage> {
        private IClientMessageHandler clientMessageHandler;

        public void setClientMessageHandler(IClientMessageHandler clientMessageHandler) {
            this.clientMessageHandler = clientMessageHandler;
        }

        @Override
        protected void writeExtraBytes(IByteBuf buf) {
            clientMessageHandler.handle(CraftTweakerAPI.client.getPlayer(), buf);
        }

        @Override
        public IMessage onMessage(Client2Server message, MessageContext ctx) {
            try {
                ZenUtilsNetworkHandler.INSTANCE.getServerMessageHandler(message.key).handle(CraftTweakerAPI.server, message.getByteBuf());
            } catch (Exception e) {
                CraftTweakerAPI.logError(null, e);
            }
            return null;
        }
    }
}
