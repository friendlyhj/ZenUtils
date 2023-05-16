package youyihj.zenutils.impl.util.sequence;

import java.util.Iterator;

/**
 * @author youyihj
 */
public class IteratorSequence<T> extends AbstractSequence<T> {
    private final Iterator<T> iterator;

    public IteratorSequence(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    protected T provide() {
        return iterator.hasNext() ? iterator.next() : endObject();
    }
}
