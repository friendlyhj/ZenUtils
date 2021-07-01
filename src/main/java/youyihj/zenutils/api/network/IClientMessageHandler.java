package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IClientMessageHandler")
@FunctionalInterface
public interface IClientMessageHandler {
    IClientMessageHandler NONE = ((player, byteBuf) -> {});

    void handle(IPlayer player, IByteBuf byteBuf);
}
