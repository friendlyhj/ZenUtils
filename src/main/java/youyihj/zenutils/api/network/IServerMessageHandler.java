package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.server.IServer;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenRegister
@ZenClass("mods.zenutils.IServerMessageHandler")
public interface IServerMessageHandler {
    IServerMessageHandler NONE = (server, byteBuf, player) -> {};

    void handle(IServer server, IByteBuf byteBuf, IPlayer player);
}
