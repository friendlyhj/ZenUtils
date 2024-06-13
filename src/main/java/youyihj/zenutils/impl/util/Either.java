package youyihj.zenutils.impl.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
public class Either<L, R> {
    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    public Either<L, R> orElseLeft(Supplier<L> newLeftSupplier) {
        if (this.left == null) {
            return new Either<>(newLeftSupplier.get(), null);
        } else {
            return this;
        }
    }

    public Either<L, R> orElseRight(Supplier<R> newRightSupplier) {
        if (this.left == null && this.right == null) {
            return new Either<>(null, newRightSupplier.get());
        } else {
            return this;
        }
    }

    public Either<L, R> validateLeft(Predicate<L> leftPredicate) {
        if (left != null && leftPredicate.test(left)) {
            return this;
        } else {
            return new Either<>(null, right);
        }
    }

    public Either<L, R> validateRight(Predicate<R> rightPredicate) {
        if (right != null && rightPredicate.test(right)) {
            return this;
        } else {
            return new Either<>(left, null);
        }
    }

    public <T> T fold(Function<L, T> leftFolder, Function<R, T> rightFolder, Supplier<T> fallback) {
        if (left != null) {
            return leftFolder.apply(left);
        }
        if (right != null) {
            return rightFolder.apply(right);
        }
        return fallback.get();
    }

    public void fold(Consumer<L> leftConsumer, Consumer<R> rightConsumer, Runnable fallback) {
        if (left != null) {
            leftConsumer.accept(left);
        } else if (right != null) {
            rightConsumer.accept(right);
        } else {
            fallback.run();
        }
    }

    public boolean isEmpty() {
        return left == null && right == null;
    }
}
