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
    void write(IByteBuf byteBuf);
}
