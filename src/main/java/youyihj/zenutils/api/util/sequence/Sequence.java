package youyihj.zenutils.api.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.sequence.Sequence")
public interface Sequence<T> {

    @ZenMethod
    <R> Sequence<R> map(Function<T, R> mapper);

    @ZenMethod
    Sequence<T> filter(Predicate<T> predicate);

    @ZenMethod
    Sequence<T> filterNot(Predicate<T> predicate);

    @ZenMethod
    Sequence<T> filterNotNull();

    @ZenMethod
    Sequence<T> limit(int size);

    @ZenMethod
    Sequence<T> distinct();

    @ZenMethod
    <K> Sequence<T> distinctBy(Function<T, K> selector);

    @ZenMethod
    <R> Sequence<R> flatMap(Function<T, Sequence<R>> mapper);

    @ZenMethod
    Sequence<T> peek(Consumer<T> consumer);

    @ZenMethod
    Sequence<T> concat(Sequence<T> other);

    @ZenMethod
    Sequence<T> sorted(Comparator<T> comparator);

    @ZenMethod
    T first();

    @ZenMethod
    void forEach(Consumer<T> consumer);

    @ZenMethod
    <A, R> R collect(Supplier<A> supplier, BiConsumer<A, T> appender, Function<A, R> finisher);

    @ZenMethod
    T reduce(T initial, BiOperator<T> operator);

    @ZenMethod
    T reduce(BiOperator<T> operator);

    @ZenMethod
    List<T> toList();

    @ZenMethod
    int count();

    @ZenMethod
    boolean isEmpty();

    @ZenMethod
    boolean allMatch(Predicate<T> predicate);

    @ZenMethod
    boolean anyMatch(Predicate<T> predicate);

    @ZenMethod
    boolean noneMatch(Predicate<T> predicate);

    @ZenMethod
    int sumToInt(ToIntFunction<T> selector);

    @ZenMethod
    double sumToDouble(ToDoubleFunction<T> selector);

    @ZenMethod
    T minBy(Comparator<T> comparator);

    @ZenMethod
    T maxBy(Comparator<T> comparator);
}
