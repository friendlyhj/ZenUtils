package youyihj.zenutils.impl.util.sequence;

import crafttweaker.CraftTweakerAPI;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.api.util.sequence.*;
import youyihj.zenutils.api.util.sequence.Comparator;

import java.util.*;

/**
 * @author youyihj
 */
public abstract class AbstractSequence<T> implements Sequence<T> {
    private static final Object END_OBJECT = new Object();

    @SuppressWarnings("unchecked")
    protected static <T> T endObject() {
        return ((T) END_OBJECT);
    }

    protected abstract T provide();

    @ZenMethod
    public <R> Sequence<R> map(Function<T, R> mapper) {
        return new SubSequence<R, T>(this) {
            @Override
            protected R provide() {
                T element = getParent().provide();
                return element == endObject() ? endObject() : mapper.apply(element);
            }
        };
    }

    @ZenMethod
    public Sequence<T> filter(Predicate<T> predicate) {
        return new SubSequence<T, T>(this) {
            @Override
            protected T provide() {
                T element = getParent().provide();
                while (element != endObject()) {
                    if (predicate.test(element)) {
                        return element;
                    }
                    element = getParent().provide();
                }
                return endObject();
            }
        };
    }

    @Override
    public Sequence<T> filterNot(Predicate<T> predicate) {
        return filter(it -> !predicate.test(it));
    }

    @Override
    public Sequence<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    @Override
    public Sequence<T> limit(int size) {
        return new SubSequence<T, T>(this) {
            private int hasProvided;

            @Override
            public T provide() {
                return hasProvided++ < size ? getParent().provide() : endObject();
            }
        };
    }

    @Override
    public Sequence<T> skip(int count) {
        return new SubSequence<T, T>(this) {
            private boolean skipped;

            @Override
            protected T provide() {
                if (!skipped) {
                    skipped = true;
                    for (int i = 0; i < count; i++) {
                        getParent().provide();
                    }
                }
                return getParent().provide();
            }
        };
    }

    @Override
    public Sequence<T> distinct() {
        return distinctBy(Function.identity());
    }

    @Override
    public <K> Sequence<T> distinctBy(Function<T, K> selector) {
        return new SubSequence<T, T>(this) {
            private final Set<K> elements = new HashSet<>();

            @Override
            protected T provide() {
                T element = getParent().provide();
                if (element == endObject()) return endObject();
                boolean dupe = !elements.add(selector.apply(element));
                return dupe ? this.provide() : element;
            }
        };
    }

    @Override
    public <R> Sequence<R> flatMap(Function<T, Sequence<R>> mapper) {
        return new SubSequence<R, T>(this) {
            private Sequence<R> child;

            @Override
            protected R provide() {
                if (child == null) {
                    T parentElement = getParent().provide();
                    if (parentElement == endObject()) return endObject();
                    child = mapper.apply(parentElement);
                }
                R element = ((AbstractSequence<R>) child).provide();
                if (element == endObject()) {
                    child = null;
                    return this.provide();
                } else {
                    return element;
                }
            }
        };
    }

    @Override
    public Sequence<T> peek(Consumer<T> consumer) {
        return new SubSequence<T, T>(this) {
            @Override
            protected T provide() {
                T element = getParent().provide();
                if (element != endObject()) {
                    consumer.apply(element);
                }
                return element;
            }
        };
    }

    @Override
    public Sequence<T> concat(Sequence<T> other) {
        return new SubSequence<T, T>(this) {
            private boolean useParent = true;

            @Override
            protected T provide() {
                AbstractSequence<T> current = useParent ? getParent() : ((AbstractSequence<T>) other);
                T element = current.provide();
                if (element == endObject() && useParent) {
                    useParent = false;
                    return this.provide();
                }
                return element;
            }
        };
    }

    @Override
    public Sequence<T> sorted(Comparator<T> comparator) {
        List<T> list = this.toList();
        list.sort(comparator::compare);
        return new IteratorSequence<>(list.iterator());
    }

    @Override
    public Sequence<T> log(Function<T, String> printMapper) {
        return peek(it -> CraftTweakerAPI.logInfo(printMapper.apply(it)));
    }

    @Override
    public Sequence<T> log() {
        return log(ZenUtilsGlobal::toString);
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        T element = this.provide();
        while (element != endObject()) {
            consumer.apply(element);
            element = this.provide();
        }
    }

    @Override
    public T first() {
        T element = provide();
        return element == endObject() ? null : element;
    }

    public <A, R> R collect(Supplier<A> supplier, BiConsumer<A, T> appender, Function<A, R> finisher) {
        A a = supplier.get();
        T element = this.provide();
        while (element != endObject()) {
            appender.apply(a, element);
            element = this.provide();
        }
        return finisher.apply(a);
    }

    @Override
    public T reduce(T initial, BiOperator<T> operator) {
        T result = initial;
        T element = this.provide();
        while (element != endObject()) {
            result = operator.apply(result, element);
            element = this.provide();
        }
        return result;
    }

    @Override
    public T reduce(BiOperator<T> operator) {
        T element = this.provide();
        T result = element;
        while (element != endObject()) {
            result = operator.apply(result, result);
            element = this.provide();
        }
        return result != endObject() ? result : null;
    }

    public List<T> toList() {
        return collect(ArrayList::new, List::add, Function.identity());
    }

    @Override
    public int count() {
        return sumToInt(it -> 1);
    }

    @Override
    public boolean isEmpty() {
        return count() == 0;
    }

    @Override
    public boolean allMatch(Predicate<T> predicate) {
        boolean result = true;
        T element = this.provide();
        while (result && element != endObject()) {
            result = predicate.test(element);
            element = this.provide();
        }
        return result;
    }

    @Override
    public boolean anyMatch(Predicate<T> predicate) {
        boolean result = false;
        T element = this.provide();
        while (!result && element != endObject()) {
            result = predicate.test(element);
            element = this.provide();
        }
        return result;
    }

    @Override
    public boolean noneMatch(Predicate<T> predicate) {
        boolean result = true;
        T element = this.provide();
        while (result && element != endObject()) {
            result = !predicate.test(element);
            element = this.provide();
        }
        return result;
    }

    @Override
    public int sumToInt(ToIntFunction<T> selector) {
        return collect(MutableInt::new, (i, e) -> i.add(selector.toInt(e)), MutableInt::getValue);
    }

    @Override
    public double sumToDouble(ToDoubleFunction<T> selector) {
        return collect(MutableDouble::new, (i, e) -> i.add(selector.toDouble(e)), MutableDouble::getValue);
    }

    @Override
    public T minBy(Comparator<T> comparator) {
        return reduce((a, b) -> comparator.compare(a, b) <= 0 ? a : b);
    }

    @Override
    public T maxBy(Comparator<T> comparator) {
        return reduce((a, b) -> comparator.compare(a, b) >= 0 ? a : b);
    }
}
