package youyihj.zenutils.impl.util.sequence;

import java.util.Iterator;
import java.util.List;

/**
 * @author youyihj
 */
public class ListSequence<T> extends AbstractSequence<T> {
    private final Iterator<T> iterator;

    public ListSequence(List<T> list) {
        this.iterator = list.iterator();
    }

    @Override
    public T provide() {
        return iterator.hasNext() ? iterator.next() : getEndObject();
    }
}
