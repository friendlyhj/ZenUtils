package youyihj.zenutils.impl.network;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataByteArray;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.player.MCPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.api.entity.ZenUtilsEntity;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.network.IByteBufWriter;
import youyihj.zenutils.api.util.CrTUUID;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author youyihj
 */
public abstract class ZenUtilsMessage implements IMessage {
    private IByteBuf byteBuf;
    protected int key = 0;
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

    public IByteBufWriter getByteBufWriter() {
        return byteBufWriter;
    }

    public void setByteBufWriter(IByteBufWriter byteBufWriter) {
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
            try {
                ZenUtilsNetworkHandler.INSTANCE.getClientMessageHandler(message.key).handle(CraftTweakerAPI.client.getPlayer(), message.getByteBuf());
            } catch (Exception e) {
                CraftTweakerAPI.logError(null, e);
            }
            return null;
        }
    }

    public static class Client2Server extends ZenUtilsMessage implements IMessageHandler<Client2Server, IMessage> {
        private boolean valid;
        private UUID playerUUID;

        @Override
        protected void readExtraBytes(IByteBuf buf) {
            this.playerUUID = buf.readUUID().getInternal();
            this.valid = Arrays.equals(ZenModule.classes.get(buf.readString()), buf.readData().asByteArray());
        }

        @Override
        protected void writeExtraBytes(IByteBuf buf) {
            IPlayer player = CraftTweakerAPI.client.getPlayer();
            CrTUUID uuid = ZenUtilsEntity.getUUIDObject(player);
            buf.writeUUID(uuid);
            writeHandlerByteCode(buf);
            getByteBufWriter().write(buf);
        }

        @Override
        public IMessage onMessage(Client2Server message, MessageContext ctx) {
            IByteBuf byteBuf = message.getByteBuf();
            Optional.ofNullable(CraftTweakerAPI.server)
                    .map(CraftTweakerMC::getMCServer)
                    .map(MinecraftServer::getPlayerList)
                    .map(playerList -> playerList.getPlayerByUUID(message.playerUUID))
                    .ifPresent(player -> {
                        if (message.valid) {
                            try {
                                ZenUtilsNetworkHandler.INSTANCE.getServerMessageHandler(message.key).handle(CraftTweakerAPI.server, byteBuf, new MCPlayer(player));
                            } catch (Exception e) {
                                CraftTweakerAPI.logError(null, e);
                            }
                        } else {
                            player.sendMessage(new TextComponentTranslation("message.zenutils.validate"));
                        }
                    });
            return null;
        }

        private void writeHandlerByteCode(IByteBuf byteBuf) {
            String className = getByteBufWriter().getClass().getName();
            byteBuf.writeString(className);
            byteBuf.writeData(new DataByteArray(Objects.requireNonNull(ZenModule.classes.get(className)), true));
        }
    }
}
