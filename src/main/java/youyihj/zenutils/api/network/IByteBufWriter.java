package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IByteBufWriter")
@FunctionalInterface
public interface IByteBufWriter {
    IByteBufWriter NONE = (byteBuf -> {});

    void write(IByteBuf byteBuf);

    // used for the default byte buffer writer of ZenNetworkHandler
    @ReflectionInvoked
    static IByteBufWriter getNone(String s) {
        return NONE;
    }
}
