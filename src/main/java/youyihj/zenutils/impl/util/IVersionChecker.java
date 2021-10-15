package youyihj.zenutils.impl.util;

/**
 * @author youyihj
 */
@FunctionalInterface
public interface IVersionChecker {
    boolean check() throws Exception;

    default boolean getResult() {
        try {
            return check();
        } catch (Exception e) {
            return false;
        }
    }
}
