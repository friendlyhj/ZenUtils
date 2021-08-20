package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

import java.util.HashMap;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.data.IData")
public class ExpandData {
    @ZenMethodStatic
    public static IData createEmptyMutableDataMap() {
        return new DataMap(new HashMap<>(), false);
    }
}
