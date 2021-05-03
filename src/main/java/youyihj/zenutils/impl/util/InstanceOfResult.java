package youyihj.zenutils.impl.util;

/**
 * @author youyihj
 */
public enum InstanceOfResult {
    A,
    B,
    UNKNOWN;

    public static <A, B> InstanceOfResult find(Class<A> aClass, Class<B> bClass, Object obj) {
        if (aClass.isInstance(obj)) return A;
        if (bClass.isInstance(obj)) return B;
        return UNKNOWN;
    }
}
