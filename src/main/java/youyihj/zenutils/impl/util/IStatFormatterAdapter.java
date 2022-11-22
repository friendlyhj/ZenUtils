package youyihj.zenutils.impl.util;

import net.minecraft.stats.IStatType;
import youyihj.zenutils.api.player.DefaultStatFormatters;
import youyihj.zenutils.api.player.IStatFormatter;

/**
 * @author youyihj
 */
public interface IStatFormatterAdapter {
    IStatFormatter adapt(IStatType type);

    class Client implements IStatFormatterAdapter {

        @Override
        public IStatFormatter adapt(IStatType type) {
            return type::format;
        }
    }

    class Server implements IStatFormatterAdapter {

        @Override
        public IStatFormatter adapt(IStatType type) {
            return DefaultStatFormatters.simple();
        }
    }
}
