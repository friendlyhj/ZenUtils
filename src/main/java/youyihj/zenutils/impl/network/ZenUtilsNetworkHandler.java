package youyihj.zenutils.impl.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import stanhebben.zenscript.ZenModule;
import stanhebben.zenscript.value.IAny;
import youyihj.zenutils.Reference;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.network.IByteBufWriter;
import youyihj.zenutils.api.network.IClientMessageHandler;
import youyihj.zenutils.api.network.IServerMessageHandler;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author youyihj
 */
public enum ZenUtilsNetworkHandler {
    INSTANCE;

    private final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
    private final Int2ObjectArrayMap<IClientMessageHandler> clientHandlers = new Int2ObjectArrayMap<>();
    private final Int2ObjectArrayMap<IServerMessageHandler> serverHandlers = new Int2ObjectArrayMap<>();

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

    public boolean isDisableScriptValidation() {
        return disableScriptValidation;
    }

    IClientMessageHandler getClientMessageHandler(int key) {
        return clientHandlers.getOrDefault(key, IClientMessageHandler.NONE);
    }

    IServerMessageHandler getServerMessageHandler(int key) {
        return serverHandlers.getOrDefault(key, IServerMessageHandler.NONE);
    }

    private void sendValidateScriptMessage(byte[] scriptBytes, String scriptClassName) {
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

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static final class ClientEventHandler {
        @SubscribeEvent
        public static void onEntityJoin(EntityJoinWorldEvent event) {
            if (ZenUtils.crafttweakerLogger.hasError() || ZenUtilsNetworkHandler.INSTANCE.serverHandlers.isEmpty())
                return;
            if (event.getWorld().isRemote && event.getEntity().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) {
                sendScriptsToServer();
            }
        }

        private static boolean isZenScriptAnonymousFunction(Class<?> scriptClass) {
            return scriptClass.getInterfaces().length == 1 && // anonymous functions only implement one interface
                    !(
                            InternalUtils.hasMethod(scriptClass, "__script__") || // plain script
                            scriptClass.isSynthetic() || // synthetic class
                            IAny.class.isAssignableFrom(scriptClass) || // IAny class
                            "__ZenMain__".equals(scriptClass.getCanonicalName()) // main class
                    );
        }

        private static void sendScriptsToServer() {
            for (Map.Entry<String, byte[]> entry : ZenModule.classes.entrySet()) {
                String name = entry.getKey();
                byte[] bytecode = entry.getValue();
                Class<?> clazz = ZenModule.loadedClasses.get(name);
                if (bytecode.length != 0 && clazz != null && isZenScriptAnonymousFunction(clazz)) {
                    ZenUtilsNetworkHandler.INSTANCE.sendValidateScriptMessage(bytecode, name);
                }
            }
        }
    }
}
