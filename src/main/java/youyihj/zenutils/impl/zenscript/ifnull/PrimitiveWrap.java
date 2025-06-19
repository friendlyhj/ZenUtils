package youyihj.zenutils.impl.zenscript.ifnull;

import stanhebben.zenscript.type.ZenType;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author youyihj
 */
public class PrimitiveWrap {
    private static final Map<ZenType, ZenType> PRIMITIVE_WRAP = InternalUtils.make(new HashMap<>(), it -> {
        it.put(ZenType.BOOL, ZenType.BOOLOBJECT);
        it.put(ZenType.BYTE, ZenType.BYTEOBJECT);
        it.put(ZenType.SHORT, ZenType.SHORTOBJECT);
        it.put(ZenType.INT, ZenType.INTOBJECT);
        it.put(ZenType.LONG, ZenType.LONGOBJECT);
        it.put(ZenType.FLOAT, ZenType.FLOATOBJECT);
        it.put(ZenType.DOUBLE, ZenType.DOUBLEOBJECT);
    });

    public static ZenType wrap(ZenType type) {
        return PRIMITIVE_WRAP.getOrDefault(type, type);
    }
}
