package youyihj.zenutils.impl.zenscript;

import youyihj.zenutils.api.event.CTEventPriority;
import youyihj.zenutils.api.network.IByteBufWriter;
import youyihj.zenutils.api.player.DefaultStatFormatters;
import youyihj.zenutils.api.player.IStatFormatter;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
public class Defaults {
    @ReflectionInvoked
    public static CTEventPriority eventPriority(String unused) {
        return CTEventPriority.NORMAL;
    }

    @ReflectionInvoked
    public static IByteBufWriter byteBufWriter(String unused) {
        return (buf) -> {};
    }

    @ReflectionInvoked
    public static IStatFormatter statFormatter(String unused) {
        return DefaultStatFormatters.simple();
    }
}
