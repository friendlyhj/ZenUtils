package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.StaticString")
@SuppressWarnings("unused")
public class StaticString {
    @ZenMethod
    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    @ZenMethod
    public static String valueOf(boolean b) {
        return String.valueOf(b);
    }

    @ZenMethod
    public static String valueOf(long l) {
        return String.valueOf(l);
    }

    @ZenMethod
    public static String valueOf(float f) {
        return String.valueOf(f);
    }

    @ZenMethod
    public static String valueOf(double d) {
        return String.valueOf(d);
    }

    @ZenMethod
    public static String valueOf(Object obj) {
        return String.valueOf(obj);
    }

    @ZenMethod
    public static String format(String format, Object... args) {
        return String.format(format, args);
    }
}
