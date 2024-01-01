package youyihj.zenutils.impl.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author youyihj
 */
public class SimpleCache<K, V> {
    private final Map<K, V> elements = new HashMap<>();
    private final Set<K> cachedKeys = new HashSet<>();
    private final Function<K, V> loader;

    public SimpleCache(Function<K, V> loader) {
        this.loader = loader;
    }

    public V get(K key) {
        if (cachedKeys.contains(key)) {
            return elements.get(key);
        } else {
            cachedKeys.add(key);
            V result = loader.apply(key);
            if (result != null) {
                elements.put(key, result);
            }
            return result;
        }
    }
}
