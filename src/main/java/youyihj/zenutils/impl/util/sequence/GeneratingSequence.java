package youyihj.zenutils.impl.util.sequence;

import youyihj.zenutils.api.util.sequence.Supplier;

/**
 * @author youyihj
 */
public class GeneratingSequence<T> extends AbstractSequence<T> {
    private final Supplier<T> supplier;

    public GeneratingSequence(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T provide() {
        return supplier.get();
    }
}
