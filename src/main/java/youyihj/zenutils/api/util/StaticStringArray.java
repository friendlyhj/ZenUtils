package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenRegister
@ZenExpansion("string[]")
@ZenClass("mods.zenutils.StaticString")
@SuppressWarnings("unused")
public class StaticStringArray {
    @ZenMethod
    public static String[] stripAll(String[] strings) {
        return StringUtils.stripAll(strings);
    }

    @ZenMethod
    public static String[] stripAll(String[] strings, String stripChars) {
        return StringUtils.stripAll(strings, stripChars);
    }
    
    @ZenMethod
    public static String[] split(String str) {
        return StringUtils.split(str);
    }

    @ZenMethod
    public static String[] split(String str, String separatorChar) {
        return StringUtils.split(str, separatorChar);
    }

    @ZenMethod
    public static String[] split(String str, String separatorChars, int max) {
        return StringUtils.split(str, separatorChars, max);
    }

    @ZenMethod
    public static String[] splitByWholeSeparator(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }

    @ZenMethod
    public static String[] splitByWholeSeparator( String str, String separator, int max) {
        return StringUtils.splitByWholeSeparator(str, separator, max);
    }

    @ZenMethod
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
    }

    @ZenMethod
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str) {
        return StringUtils.splitPreserveAllTokens(str);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str, String separatorChar) {
        return StringUtils.splitPreserveAllTokens(str, separatorChar);
    }

    @ZenMethod
    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return StringUtils.splitPreserveAllTokens(str, separatorChars, max);
    }

    @ZenMethod
    public static String[] splitByCharacterType(String str) {
        return StringUtils.splitByCharacterType(str);
    }

    @ZenMethod
    public static String[] splitByCharacterTypeCamelCase(String str) {
        return StringUtils.splitByCharacterTypeCamelCase(str);
    }

}