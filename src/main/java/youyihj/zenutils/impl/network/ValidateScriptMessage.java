package youyihj.zenutils.impl.network;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.api.network.IByteBuf;

import java.util.Arrays;

/**
 * @author youyihj
 */
public class ValidateScriptMessage extends ZenUtilsMessage {
    private int scriptBytesHash;
    private String scriptClassName;

    public ValidateScriptMessage() {}

    public ValidateScriptMessage(byte[] scriptBytes, String scriptClassName) {
        setKey("validate_script");
        this.scriptBytesHash = Arrays.hashCode(scriptBytes);
        this.scriptClassName = scriptClassName;
    }

    @Override
    protected void writeExtraBytes(IByteBuf buf) {
        buf.writeString(scriptClassName);
        buf.writeInt(scriptBytesHash);
    }

    @Override
    protected void readExtraBytes(IByteBuf buf) {
        this.scriptClassName = buf.readString();
        this.scriptBytesHash = buf.readInt();
    }

    public static class Handler implements IMessageHandler<ValidateScriptMessage, IMessage> {

        public Handler() {}

        @Override
        public IMessage onMessage(ValidateScriptMessage message, MessageContext ctx) {
            byte[] serverScriptBytes = ZenModule.classes.get(message.scriptClassName);
            if (serverScriptBytes == null || Arrays.hashCode(serverScriptBytes) != message.scriptBytesHash) {
                ctx.getServerHandler().disconnect(new TextComponentTranslation("message.zenutils.validate"));
            }
            return null;
        }
    }
}
