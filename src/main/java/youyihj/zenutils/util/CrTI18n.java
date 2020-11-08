package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.client.resources.I18n;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.I18n")
@SuppressWarnings("unused")
public class CrTI18n {

    @ZenMethod
    public static boolean hasKey(String key) {
        return I18n.hasKey(key);
    }

    @ZenMethod
    public static String format(String format, String... args) {
        return I18n.format(format, args);
    }
}
