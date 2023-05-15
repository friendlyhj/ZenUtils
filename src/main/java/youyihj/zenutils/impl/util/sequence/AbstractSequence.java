package youyihj.zenutils.impl.util.sequence;

import org.apache.commons.lang3.mutable.MutableInt;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.sequence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public abstract class AbstractSequence<T> implements Sequence<T> {
    private static final Object END_OBJECT = new Object();

    @SuppressWarnings("unchecked")
    protected static <T> T getEndObject() {
        return ((T) END_OBJECT);
    }

    public <A, R> R collect(Supplier<A> supplier, BiConsumer<A, T> appender, Function<A, R> finisher) {
        A a = supplier.get();
        T element = this.provide();
        while (element != getEndObject()) {
            appender.apply(a, element);
            element = this.provide();
        }
        return finisher.apply(a);
    }

    public List<T> toList() {
        return collect(() -> new ArrayList<T>(), List::add, it -> it);
    }

    @Override
    public int count() {
        return collect(MutableInt::new, (i, e) -> i.increment(), MutableInt::getValue);
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        T element = this.provide();
        while (element != getEndObject()) {
            consumer.apply(element);
            element = this.provide();
        }
    }

    @ZenMethod
    public <R> Sequence<R> map(Function<T, R> mapper) {
        return new SubSequence<R, T>(this) {
            @Override
            public R provide() {
                T element = getParent().provide();
                return element == getEndObject() ? getEndObject() : mapper.apply(element);
            }
        };
    }

    @ZenMethod
    public Sequence<T> filter(Predicate<T> predicate) {
        return new SubSequence<T, T>(this) {
            @Override
            public T provide() {
                T element = getParent().provide();
                while (element != getEndObject()) {
                    if (predicate.test(element)) {
                        return element;
                    }
                    element = getParent().provide();
                }
                return getEndObject();
            }
        };
    }
}
