package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.text.translation.I18n;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.I18n")
@SuppressWarnings({"unused", "deprecation"})
public class CrTI18n {

    @ZenMethod
    public static boolean hasKey(String key) {
        return I18n.canTranslate(key);
    }

    @ZenMethod
    public static String format(String format, Object... args) {
        return I18n.translateToLocalFormatted(format, args);
    }

    @ZenMethod
    public static String format(String format) {
        return I18n.translateToLocal(format);
    }
}
