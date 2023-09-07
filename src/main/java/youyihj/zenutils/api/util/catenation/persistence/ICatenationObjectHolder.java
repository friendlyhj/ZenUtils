package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.api.data.IData;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.impl.util.catenation.persistence.ObjectHolderTypeRegistry;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
public interface ICatenationObjectHolder<T> {
    Type<T> getType();

    IData serializeToData();

    void deserializeFromData(IData data);

    void receiveObject(T object);

    T getValue();

    void setValue(T value);

    default boolean isValid(Catenation catenation) {
        return getValue() != null;
    }

    final class Type<T> {
        private final Supplier<ICatenationObjectHolder<T>> factory;
        private final Class<T> valueType;

        private Type(Supplier<ICatenationObjectHolder<T>> factory, Class<T> valueType) {
            this.factory = factory;
            this.valueType = valueType;
        }

        public static <T> Type<T> of(Supplier<ICatenationObjectHolder<T>> factory, Class<T> valueType) {
            Type<T> type = new Type<>(factory, valueType);
            ObjectHolderTypeRegistry.register(type);
            return type;
        }

        public ICatenationObjectHolder<T> createHolder() {
            return factory.get();
        }

        public Class<T> getValueType() {
            return valueType;
        }
    }

    final class Key<T> {
        private final String key;
        private final Type<T> type;

        private Key(String key, Type<T> type) {
            this.key = key;
            this.type = type;
        }

        public static <T> Key<T> of(String key, Type<T> type) {
            return new Key<>(key, type);
        }

        public String getKey() {
            return key;
        }

        public Type<T> getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key<?> key1 = (Key<?>) o;
            return Objects.equals(key, key1.key) && Objects.equals(type, key1.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, type);
        }
    }
}
