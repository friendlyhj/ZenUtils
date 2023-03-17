package youyihj.zenutils.impl.util.catenation.persistence;

import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author youyihj
 */
public class ObjectHolderTypeRegistry {
    private static final Map<String, Type<?>> types = new HashMap<>();

    public static void register(Type<?> type) {
        types.put(type.getValueType().getName(), type);
    }

    public static Type<?> get(String typeName) {
        return types.get(typeName);
    }
}
