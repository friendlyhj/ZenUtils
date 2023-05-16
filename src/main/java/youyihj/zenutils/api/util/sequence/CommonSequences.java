package youyihj.zenutils.api.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.impl.util.sequence.ArraySequence;
import youyihj.zenutils.impl.util.sequence.GeneratingSequence;
import youyihj.zenutils.impl.util.sequence.IteratorSequence;

import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("mods.zenutils.sequence.Sequence")
public class CommonSequences {
    @ZenMethodStatic
    public static <T> Sequence<T> fromList(List<T> list) {
        return new IteratorSequence<>(list.iterator());
    }

    @ZenMethodStatic
    public static <T> Sequence<T> fromArray(T[] array) {
        return new ArraySequence<>(array);
    }

    @ZenMethodStatic
    public static <K, V> Sequence<K> fromMapKeys(Map<K, V> map) {
        return new IteratorSequence<>(map.keySet().iterator());
    }

    @ZenMethodStatic
    public static <K, V> Sequence<V> fromMapValues(Map<K, V> map) {
        return new IteratorSequence<>(map.values().iterator());
    }

    @ZenMethodStatic
    public static <K, V> Sequence<Map.Entry<K, V>> fromMap(Map<K, V> map) {
        return new IteratorSequence<>(map.entrySet().iterator());
    }

    @ZenMethodStatic
    public static <T> Sequence<T> generating(Supplier<T> supplier) {
        return new GeneratingSequence<>(supplier);
    }
}
