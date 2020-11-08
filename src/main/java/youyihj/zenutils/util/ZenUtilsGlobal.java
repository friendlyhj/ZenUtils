package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ZenUtils")
@ZenRegister
@SuppressWarnings("unused")
public class ZenUtilsGlobal {
    private ZenUtilsGlobal() {

    }

    @ZenMethod
    public static String typeof(Object object) {
        return (object == null) ? "null" : object.getClass().getName();
    }

    @ZenMethod
    public static String toString(Object object) {
        return String.valueOf(object);
    }

    /** Actually, these below are not global */
    @ZenMethod
    public static String typeof(int unused) {
        return "int";
    }

    @ZenMethod
    public static String typeof(byte unused) {
        return "byte";
    }

    @ZenMethod
    public static String typeof(short unused) {
        return "short";
    }

    @ZenMethod
    public static String typeof(long unused) {
        return "long";
    }

    @ZenMethod
    public static String typeof(char unused) {
        return "char";
    }

    @ZenMethod
    public static String typeof(float unused) {
        return "float";
    }

    @ZenMethod
    public static String typeof(double unused) {
        return "double";
    }

    @ZenMethod
    public static String typeof(boolean unused) {
        return "boolean";
    }
}
