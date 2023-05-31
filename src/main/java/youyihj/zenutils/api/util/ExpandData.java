package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.impl.util.DeepDataUpdater;

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

    @ZenMethod
    public static IData deepUpdate(IData data, IData toUpdate, @Optional IData updateOperation) {
        return DeepDataUpdater.deepUpdate(data, toUpdate, updateOperation);
    }

    @ZenRegister
    @ZenClass("mods.zenutils.DataUpdateOperation")
    public static class DataUpdateOperation {
        @ZenProperty
        public static final IData OVERWRITE = new DataInt(DeepDataUpdater.Operation.OVERWRITE);
        @ZenProperty
        public static final IData APPEND = new DataInt(DeepDataUpdater.Operation.APPEND);
        @ZenProperty
        public static final IData MERGE = new DataInt(DeepDataUpdater.Operation.MERGE);
        @ZenProperty
        public static final IData REMOVE = new DataInt(DeepDataUpdater.Operation.REMOVE);
        @ZenProperty
        public static final IData BUMP = new DataInt(DeepDataUpdater.Operation.BUMP);
    }
}
