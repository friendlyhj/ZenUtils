package youyihj.zenutils.impl.network;

import crafttweaker.api.data.DataByteArray;
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
    private byte[] scriptBytes;
    private String scriptClassName;

    public ValidateScriptMessage() {}

    public ValidateScriptMessage(byte[] scriptBytes, String scriptClassName) {
        setKey("validate_script");
        this.scriptBytes = scriptBytes;
        this.scriptClassName = scriptClassName;
    }

    @Override
    protected void writeExtraBytes(IByteBuf buf) {
        buf.writeString(scriptClassName);
        buf.writeData(new DataByteArray(scriptBytes, true));
    }

    @Override
    protected void readExtraBytes(IByteBuf buf) {
        this.scriptClassName = buf.readString();
        this.scriptBytes = buf.readData().asByteArray();
    }

    public static class Handler implements IMessageHandler<ValidateScriptMessage, IMessage> {

        public Handler() {}

        @Override
        public IMessage onMessage(ValidateScriptMessage message, MessageContext ctx) {
            if (!Arrays.equals(ZenModule.classes.get(message.scriptClassName), message.scriptBytes)) {
                ctx.getServerHandler().disconnect(new TextComponentTranslation("message.zenutils.validate"));
            }
            return null;
        }
    }
}
