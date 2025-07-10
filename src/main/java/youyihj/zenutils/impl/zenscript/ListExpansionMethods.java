package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenTypeUtil;

import java.util.*;

/**
 * @author youyihj
 */
public class ListExpansionMethods {
    public static final IJavaMethod ADD = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, List.class, "add", Object.class);
    public static final IJavaMethod ADD_ALL = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "addAll", List.class, Object[].class);
    public static final IJavaMethod CLONE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "clone", List.class);
    public static final IJavaMethod CLEAR = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, List.class, "clear");
    public static final IJavaMethod INDEX_OF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, List.class, "indexOf", Object.class);
    public static final IJavaMethod IS_EMPTY = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, List.class, "isEmpty");
    public static final IJavaMethod IS_NOT_EMPTY = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "isNotEmpty", List.class);
    public static final List<IJavaMethod> IS_SORTED = Arrays.asList(
            JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "isSorted", List.class),
            JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "isSorted", List.class, Comparator.class)
    );
    public static final IJavaMethod LAST_INDEX_OF = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, List.class, "lastIndexOf", Object.class);
    public static final IJavaMethod REMOVE_ALL = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "removeAll", List.class, Object[].class);
    public static final IJavaMethod REMOVE_ALL_OCCURRENCES = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "removeAllOccurrences", List.class, Object.class);
    public static final IJavaMethod REMOVE_BY_INDEX = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, ListHelper.class, "removeByIndex", List.class, int[].class);
    public static final IJavaMethod REVERSE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collections.class, "reverse", List.class);
    public static final List<IJavaMethod> SORT = Arrays.asList(
            JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collections.class, "sort", List.class),
            JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collections.class, "sort", List.class, Comparator.class)
    );
    public static final IJavaMethod SHIFT = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collections.class, "rotate", List.class, int.class);
    public static final IJavaMethod SWAP = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Collections.class, "swap", List.class, int.class, int.class);
}
