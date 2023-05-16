package youyihj.zenutils.impl.util.sequence;

/**
 * @author youyihj
 */
public abstract class SubSequence<T, P> extends AbstractSequence<T> {
    private final AbstractSequence<P> parent;

    public SubSequence(AbstractSequence<P> parent) {
        this.parent = parent;
    }

    public AbstractSequence<P> getParent() {
        return parent;
    }
}
