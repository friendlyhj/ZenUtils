package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IByteBufWriter")
@FunctionalInterface
public interface IByteBufWriter {
    IByteBufWriter NONE = (byteBuf -> {});

    void write(IByteBuf byteBuf);

    @SuppressWarnings("unused")
    // used for the default byte buffer writer of ZenNetworkHandler
    static IByteBufWriter getNone(String s) {
        return NONE;
    }
}
