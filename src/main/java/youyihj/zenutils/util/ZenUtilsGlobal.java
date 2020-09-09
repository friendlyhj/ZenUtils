package youyihj.zenutils.util;

@SuppressWarnings("unused")
public class ZenUtilsGlobal {
    private ZenUtilsGlobal() {

    }

    public static String typeof(Object object) {
        return (object == null) ? "null" : object.getClass().getName();
    }

    public static String toString(Object object) {
        return String.valueOf(object);
    }
}
