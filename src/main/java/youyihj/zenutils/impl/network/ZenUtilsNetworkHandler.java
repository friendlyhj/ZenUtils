package youyihj.zenutils.impl.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.network.IByteBufWriter;
import youyihj.zenutils.api.network.IClientMessageHandler;
import youyihj.zenutils.api.network.IServerMessageHandler;

import java.util.Objects;

/**
 * @author youyihj
 */
public enum ZenUtilsNetworkHandler {
    INSTANCE;

    private final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
    private final Int2ObjectOpenHashMap<IClientMessageHandler> clientHandlers = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<IServerMessageHandler> serverHandlers = new Int2ObjectOpenHashMap<>();

    private boolean disableScriptValidation;

    ZenUtilsNetworkHandler() {
        channel.registerMessage(ZenUtilsMessage.Server2Client.class, ZenUtilsMessage.Server2Client.class, 0, Side.CLIENT);
        channel.registerMessage(ZenUtilsMessage.Client2Server.class, ZenUtilsMessage.Client2Server.class, 1, Side.SERVER);
        channel.registerMessage(ValidateScriptMessage.Handler.class, ValidateScriptMessage.class, 2, Side.SERVER);
    }

    public void registerServer2ClientMessage(String key, IClientMessageHandler clientMessageHandler) {
        clientHandlers.put(key.hashCode(), clientMessageHandler);
    }

    public void registerClient2ServerMessage(String key, IServerMessageHandler serverMessageHandler) {
        serverHandlers.put(key.hashCode(), serverMessageHandler);
    }

    public void sendToDimension(String key, IByteBufWriter byteBufWriter, int dimensionID) {
        channel.sendToDimension(getServer2ClientMessage(key, byteBufWriter), dimensionID);
    }

    public void sendToAll(String key, IByteBufWriter byteBufWriter) {
        channel.sendToAll(getServer2ClientMessage(key, byteBufWriter));
    }

    public void sendToAllAround(String key, IByteBufWriter byteBufWriter, double x, double y, double z, double range, int dimensionID) {
        channel.sendToAllAround(getServer2ClientMessage(key, byteBufWriter), new NetworkRegistry.TargetPoint(dimensionID, x, y, z, range));
    }

    public void sendTo(String key, IByteBufWriter byteBufWriter, EntityPlayerMP player) {
        channel.sendTo(getServer2ClientMessage(key, byteBufWriter), player);
    }

    public void sendToServer(String key, IByteBufWriter byteBufWriter) {
        channel.sendToServer(getClient2ServerMessage(key, byteBufWriter));
    }

    public void disableScriptValidation() {
        disableScriptValidation = true;
    }

    public boolean shouldSendScripts() {
        return !serverHandlers.isEmpty();
    }

    public boolean shouldValidateScripts(MinecraftServer server) {
        return !disableScriptValidation && !serverHandlers.isEmpty() && server.isDedicatedServer();
    }

    IClientMessageHandler getClientMessageHandler(int key) {
        return clientHandlers.getOrDefault(key, IClientMessageHandler.NONE);
    }

    IServerMessageHandler getServerMessageHandler(int key) {
        return serverHandlers.getOrDefault(key, IServerMessageHandler.NONE);
    }

    void sendValidateScriptMessage(byte[] scriptBytes, String scriptClassName) {
        channel.sendToServer(new ValidateScriptMessage(scriptBytes, scriptClassName));
    }

    private ZenUtilsMessage.Server2Client getServer2ClientMessage(String key, IByteBufWriter byteBufWriter) {
        ZenUtilsMessage.Server2Client message = new ZenUtilsMessage.Server2Client();
        message.setKey(key);
        message.setByteBufWriter(Objects.requireNonNull(byteBufWriter));
        return message;
    }

    private ZenUtilsMessage.Client2Server getClient2ServerMessage(String key, IByteBufWriter byteBufWriter) {
        ZenUtilsMessage.Client2Server message = new ZenUtilsMessage.Client2Server();
        message.setKey(key);
        message.setByteBufWriter(Objects.requireNonNull(byteBufWriter));
        return message;
    }
}
