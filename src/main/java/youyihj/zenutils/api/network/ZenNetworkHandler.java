package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.network.ZenUtilsNetworkHandler;

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
    public static void sendToDimension(String key, int dimensionID, IServerMessageHandler serverMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.sendToDimension(key, serverMessageHandler, dimensionID);
    }

    @ZenMethod
    public static void sendToAll(String key, IServerMessageHandler serverMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.sendToAll(key, serverMessageHandler);
    }

    @ZenMethod
    public static void sendToAllAround(String key, double x, double y, double z, double range, int dimensionID, IServerMessageHandler serverMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.sendToAllAround(key, serverMessageHandler, x, y, z, range, dimensionID);
    }

    @ZenMethod
    public static void sendTo(String key, IPlayer player, IServerMessageHandler serverMessageHandler) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        if (mcPlayer instanceof EntityPlayerMP) {
            ZenUtilsNetworkHandler.INSTANCE.sendTo(key, serverMessageHandler, ((EntityPlayerMP) mcPlayer));
        }
    }

    @ZenMethod
    public static void sendToServer(String key, IClientMessageHandler clientMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.sendToServer(key, clientMessageHandler);
    }

    @ZenMethod
    public static void registerClient2ServerMessage(String key, IServerMessageHandler serverMessageHandler) {
        ZenUtilsNetworkHandler.INSTANCE.registerClient2ServerMessage(key, serverMessageHandler);
    }
}
