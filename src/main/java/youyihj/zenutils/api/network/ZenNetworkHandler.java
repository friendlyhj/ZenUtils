package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.network.ZenUtilsNetworkHandler;
import youyihj.zenutils.impl.zenscript.Defaults;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.NetworkHandler")
public class ZenNetworkHandler {
    @ZenMethod
    public static void registerServer2ClientMessage(String key, IClientMessageHandler clientMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.registerServer2ClientMessage(key, clientMessageHandler);
    }

    @ZenMethod
    public static void sendToDimension(String key, int dimensionID, @Optional(methodClass = Defaults.class, methodName = "byteBufWriter") IByteBufWriter byteBufWriter) {
        ZenUtilsNetworkHandler.INSTANCE.sendToDimension(key, byteBufWriter, dimensionID);
    }

    @ZenMethod
    public static void sendToAll(String key, @Optional(methodClass = Defaults.class, methodName = "byteBufWriter") IByteBufWriter byteBufWriter) {
        ZenUtilsNetworkHandler.INSTANCE.sendToAll(key, byteBufWriter);
    }

    @ZenMethod
    public static void sendToAllAround(String key, double x, double y, double z, double range, int dimensionID, @Optional(methodClass = Defaults.class, methodName = "byteBufWriter") IByteBufWriter byteBufWriter) {
        ZenUtilsNetworkHandler.INSTANCE.sendToAllAround(key, byteBufWriter, x, y, z, range, dimensionID);
    }

    @ZenMethod
    public static void sendTo(String key, IPlayer player, @Optional(methodClass = Defaults.class, methodName = "byteBufWriter") IByteBufWriter byteBufWriter) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        if (mcPlayer instanceof EntityPlayerMP) {
            ZenUtilsNetworkHandler.INSTANCE.sendTo(key, byteBufWriter, ((EntityPlayerMP) mcPlayer));
        }
    }

    @ZenMethod
    public static void sendToServer(String key, @Optional(methodClass = Defaults.class, methodName = "byteBufWriter") IByteBufWriter byteBufWriter) {
        ZenUtilsNetworkHandler.INSTANCE.sendToServer(key, byteBufWriter);
    }

    @ZenMethod
    public static void registerClient2ServerMessage(String key, IServerMessageHandler serverMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.registerClient2ServerMessage(key, serverMessageHandler);
    }

    @ZenMethod
    public static void disableScriptValidation() {
        ZenUtilsNetworkHandler.INSTANCE.disableScriptValidation();
    }
}
