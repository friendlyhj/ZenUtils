package youyihj.zenutils.impl.network;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import youyihj.zenutils.api.entity.ZenUtilsEntity;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.network.IClientMessageHandler;
import youyihj.zenutils.api.network.IServerMessageHandler;
import youyihj.zenutils.api.util.CrTUUID;

import java.util.Optional;
import java.util.UUID;

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
            IPlayer player = CraftTweakerAPI.client.getPlayer();
            CrTUUID uuid = ZenUtilsEntity.getUUIDObject(player);
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
            clientMessageHandler.handle(player, buf);
        }

        @Override
        public IMessage onMessage(Client2Server message, MessageContext ctx) {
            IByteBuf byteBuf = message.getByteBuf();
            UUID uuid = new UUID(byteBuf.readLong(), byteBuf.readLong());
            if (!ScriptValidator.getValidateResult(uuid)) {
                Optional.ofNullable(CraftTweakerAPI.server)
                        .map(CraftTweakerMC::getMCServer)
                        .map(MinecraftServer::getPlayerList)
                        .map(playerList -> playerList.getPlayerByUUID(uuid))
                        .ifPresent(player -> {
                            player.sendMessage(new TextComponentTranslation("message.zenutils.validate"));
                        });
            }
            try {
                ZenUtilsNetworkHandler.INSTANCE.getServerMessageHandler(message.key).handle(CraftTweakerAPI.server, byteBuf);
            } catch (Exception e) {
                CraftTweakerAPI.logError(null, e);
            }
            return null;
        }
    }
}
