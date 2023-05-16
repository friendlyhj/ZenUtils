package youyihj.zenutils.impl.util.sequence;

/**
 * @author youyihj
 */
public class ArraySequence<T> extends AbstractSequence<T> {
    private final T[] array;
    private int cursor;

    public ArraySequence(T[] array) {
        this.array = array;
    }

    @Override
    protected T provide() {
        if (cursor >= array.length) return endObject();
        return array[cursor++];
    }
}
