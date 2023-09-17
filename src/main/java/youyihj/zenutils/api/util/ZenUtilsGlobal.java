package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.reload.ScriptReloader;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.Arrays;

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

    @ZenMethod
    public static void addRegexLogFilter(String regex) {
        ZenUtils.crafttweakerLogger.addRegexLogFilter(regex);
    }

    @ZenMethod
    public static Object[] arrayOf(int length, @Optional Object value) {
        Object[] array = new Object[length];
        if (value != null) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static int[] intArrayOf(int length, @Optional int value) {
        int[] array = new int[length];
        if (value != 0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static byte[] byteArrayOf(int length, @Optional byte value) {
        byte[] array = new byte[length];
        if (value != 0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static short[] shortArrayOf(int length, @Optional short value) {
        short[] array = new short[length];
        if (value != 0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static long[] longArrayOf(int length, @Optional long value) {
        long[] array = new long[length];
        if (value != 0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static float[] floatArrayOf(int length, @Optional float value) {
        float[] array = new float[length];
        if (value != 0.0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static double[] doubleArrayOf(int length, @Optional double value) {
        double[] array = new double[length];
        if (value != 0.0) {
            Arrays.fill(array, value);
        }
        return array;
    }

    @ZenMethod
    public static boolean[] boolArrayOf(int length, @Optional boolean value) {
        boolean[] array = new boolean[length];
        if (value) {
            Arrays.fill(array, true);
        }
        return array;
    }

    @ZenMethod
    public static int scriptStatus() {
        return InternalUtils.getScriptStatus().ordinal();
    }

    @ZenMethod
    public static void addReloadableLoader(String loaderName) {
        if (loaderName.equals("preinit") || loaderName.equals("contenttweaker")) {
            throw new IllegalArgumentException("This loader is not reloadable");
        }
        ScriptReloader.addReloadableLoader(loaderName);
    }

    // Actually, these below are not global
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
