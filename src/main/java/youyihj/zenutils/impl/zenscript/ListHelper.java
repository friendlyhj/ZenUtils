package youyihj.zenutils.impl.zenscript;

import youyihj.zenutils.api.util.ReflectionInvoked;

import java.util.*;

/**
 * Provides a mirror of the Apache Commons Lang ArrayUtils methods for List operations that are not available in the standard Java Collection API.
 *
 * @author youyihj
 */
@ReflectionInvoked
public class ListHelper {
    @SafeVarargs
    @ReflectionInvoked
    public static <E> void addAll(List<E> list, E... elements) {
        list.addAll(Arrays.asList(elements));
    }

    @SafeVarargs
    @ReflectionInvoked
    public static <E> void addAll(List<E> list, int index, E... elements) {
        list.addAll(index, Arrays.asList(elements));
    }

    @ReflectionInvoked
    public static <E> List<E> clone(List<E> list) {
        return new ArrayList<>(list);
    }

    @ReflectionInvoked
    public static boolean isNotEmpty(List<?> list) {
        return !list.isEmpty();
    }

    @ReflectionInvoked
    public static <E> boolean isSorted(List<E> list, Comparator<E> comparator) {
        if (list.size() < 2) {
            return true;
        }
        E previous = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            final E current = list.get(i);
            if (comparator.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    @ReflectionInvoked
    public static <E extends Comparable<E>> boolean isSorted(List<E> list) {
        return isSorted(list, Comparator.naturalOrder());
    }

    @ReflectionInvoked
    public static <E> boolean removeAllOccurrences(List<E> list, E element) {
        return list.removeIf(e -> Objects.equals(e, element));
    }

    @ReflectionInvoked
    public static <E> void removeByIndex(List<E> list, int... indexes) {
        if (indexes.length == 0) {
            return;
        }
        if (indexes.length == 1) {
            list.remove(indexes[0]);
            return;
        }
        indexes = Arrays.stream(indexes).distinct().sorted().toArray();
        if (indexes[0] < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + indexes[0]);
        }
        if (indexes[indexes.length - 1] >= list.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + indexes[indexes.length - 1]);
        }
        for (int i = 0; i < indexes.length; i++) {
            list.remove(indexes[i] - i);
        }
    }

    @ReflectionInvoked
    @SafeVarargs
    public static <E> void removeAll(List<E> list, E... elements) {
        list.removeAll(Arrays.asList(elements));
    }

}
