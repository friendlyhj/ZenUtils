package youyihj.zenutils.impl.network;

import crafttweaker.api.data.DataByteArray;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import youyihj.zenutils.api.network.IByteBuf;

import java.util.UUID;

/**
 * @author youyihj
 */
public final class ValidateScriptMessage extends ZenUtilsMessage.Client2Server {
    private byte[] scriptBytes;
    private String scriptName;
    private UUID playerUUID;

    public ValidateScriptMessage(byte[] scriptBytes, String scriptName, UUID playerUUID) {
        this.scriptBytes = scriptBytes;
        this.scriptName = scriptName;
        this.playerUUID = playerUUID;
    }

    public ValidateScriptMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        IByteBuf byteBuf = getByteBuf();
        playerUUID = new UUID(byteBuf.readLong(), byteBuf.readLong());
        scriptName = byteBuf.readString();
        scriptBytes = byteBuf.readData().asByteArray();
    }

    @Override
    protected void writeExtraBytes(IByteBuf buf) {
        buf.writeLong(playerUUID.getMostSignificantBits());
        buf.writeLong(playerUUID.getLeastSignificantBits());
        buf.writeString(scriptName);
        buf.writeData(new DataByteArray(scriptBytes, true));
    }

    public static class Handler implements IMessageHandler<ValidateScriptMessage, IMessage> {

        @Override
        public IMessage onMessage(ValidateScriptMessage message, MessageContext ctx) {
            ScriptValidator.validate(message.playerUUID, message.scriptName, message.scriptBytes);
            return null;
        }
    }
}
