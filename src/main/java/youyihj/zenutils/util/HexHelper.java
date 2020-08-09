package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.HexHelper")
@SuppressWarnings("unused")
public class HexHelper {
    @ZenMethod
    public static String toHexString(int i) {
        return Integer.toHexString(i);
    }

    @ZenMethod
    public static int toDecInteger(String hex) {
        return Integer.parseInt(hex, 16);
    }
}
