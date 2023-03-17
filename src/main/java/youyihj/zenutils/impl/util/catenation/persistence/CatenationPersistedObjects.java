package youyihj.zenutils.impl.util.catenation.persistence;

import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author youyihj
 */
public class CatenationPersistedObjects {
    private final Map<ICatenationObjectHolder.Key<?>, Object> objects = new HashMap<>();
    private final Set<String> keys = new HashSet<>();

    public <T> void with(String key, ICatenationObjectHolder.Type<T> type, T object) {
        if (!keys.add(key)) {
            throw new IllegalArgumentException("Key " + key + " already exists");
        }
        objects.put(ICatenationObjectHolder.Key.of(key, type), object);
    }

    public Map<ICatenationObjectHolder.Key<?>, Object> getObjects() {
        return objects;
    }
}
