package youyihj.zenutils.impl.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stanhebben.zenscript.ZenModule;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author youyihj
 */
public class ValidateScriptMessage implements IMessage {
    private int scriptBytesHash;
    private String scriptClassName;

    public ValidateScriptMessage() {}

    public ValidateScriptMessage(byte[] scriptBytes, String scriptClassName) {
        this.scriptBytesHash = Arrays.hashCode(scriptBytes);
        this.scriptClassName = scriptClassName;
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeCharSequence(scriptClassName, StandardCharsets.UTF_8);
        byteBuf.writeInt(scriptBytesHash);
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.scriptClassName = byteBuf.readCharSequence(byteBuf.readableBytes() - 4, StandardCharsets.UTF_8).toString();
        this.scriptBytesHash = byteBuf.readInt();
    }

    public static class Handler implements IMessageHandler<ValidateScriptMessage, IMessage> {

        public Handler() {}

        @Override
        public IMessage onMessage(ValidateScriptMessage message, MessageContext ctx) {
            byte[] serverScriptBytes = ZenModule.classes.get(message.scriptClassName);
            if (serverScriptBytes != null && Arrays.hashCode(serverScriptBytes) == message.scriptBytesHash) {
                PlayerScriptValidation.ServerEventHandler.validatedScriptsByPlayer.put(ctx.getServerHandler().player, message.scriptClassName);
            }
            return null;
        }
    }
}
