package youyihj.zenutils.impl.util.sequence;

import youyihj.zenutils.api.util.sequence.Sequence;

/**
 * @author youyihj
 */
public abstract class SubSequence<T, P> extends AbstractSequence<T> {
    private final Sequence<P> parent;

    public SubSequence(Sequence<P> parent) {
        this.parent = parent;
    }

    public Sequence<P> getParent() {
        return parent;
    }
}
