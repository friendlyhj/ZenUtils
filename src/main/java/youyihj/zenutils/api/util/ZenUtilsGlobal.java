package youyihj.zenutils.api.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.logger.RegexLogFilter;
import youyihj.zenutils.impl.reload.ScriptReloader;
import youyihj.zenutils.impl.runtime.ZenUtilsTweaker;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ZenUtils")
@ZenRegister
@SuppressWarnings("unused")
public class ZenUtilsGlobal {
    private ZenUtilsGlobal() {

    }

    private static final ToStringStyle ARRAY_TO_STRING_STYLE = InternalUtils.make(new StandardToStringStyle(), it -> {
        it.setUseClassName(false);
        it.setUseIdentityHashCode(false);
        it.setUseFieldNames(false);
        it.setContentStart(StringUtils.EMPTY);
        it.setContentEnd(StringUtils.EMPTY);
        it.setArrayStart("[");
        it.setArrayEnd("]");
    });

    @ZenMethod
    public static String typeof(Object object) {
        return (object == null) ? "null" : object.getClass().getCanonicalName();
    }

    @ZenMethod
    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (object.getClass().isArray()) {
            return new ToStringBuilder(object, ARRAY_TO_STRING_STYLE).append(object).toString();
        }
        if (object instanceof List) {
            return ((List<?>) object).stream().map(ZenUtilsGlobal::toString).collect(Collectors.joining(",", "[", "]"));
        }
        return object.toString();
    }

    @ZenMethod
    public static void addRegexLogFilter(String regex) {
        ZenUtils.crafttweakerLogger.getLogOption().addFilter(new RegexLogFilter(Pattern.compile(regex)));
    }

    @ZenMethod
    public static Object[] arrayOf(int length, @Optional Object value) {
        Object[] array = new Object[length];
        if (value != null) {
            if (!value.getClass().isArray()) {
                Arrays.fill(array, value);
            } else {
                for (int i = 0; i < array.length; i++) {
                    array[i] = InternalUtils.cloneArray(value);
                }
            }
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
    public static String currentLoader() {
        return ((ZenUtilsTweaker) CraftTweakerAPI.tweaker).getCurrentLoader();
    }

    @ZenMethod
    public static void addReloadableLoader(String loaderName) {
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
