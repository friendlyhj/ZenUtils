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

    T provide();

    @ZenMethod
    <A, R> R collect(Supplier<A> supplier, BiConsumer<A, T> appender, Function<A, R> finisher);

    @ZenMethod
    List<T> toList();

    @ZenMethod
    int count();

    @ZenMethod
    void forEach(Consumer<T> consumer);

    @ZenMethod
    <R> Sequence<R> map(Function<T, R> mapper);

    @ZenMethod
    Sequence<T> filter(Predicate<T> predicate);
}
