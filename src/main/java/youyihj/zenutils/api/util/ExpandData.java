package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.impl.util.DeepDataUpdater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    @ZenMethodStatic
    public static IData createEmptyMutableDataList() {
        return new DataList(new ArrayList<>(), false);
    }

    @ZenMethodStatic
    public static IData createDataList(List<IData> dataList) {
        return new DataList(dataList, true);
    }

    @ZenMethodStatic
    public static IData createDataList(IData[] dataArray) {
        return new DataList(Arrays.asList(dataArray), true);
    }

    @ZenMethod
    public static IData setAtCopied(IData dataList, int index, IData element) {
        IData[] operatorsArray = dataArrayOf(index + 1, DataUpdateOperation.APPEND);
        operatorsArray[index] = DataUpdateOperation.OVERWRITE;
        IData[] toUpdateArray = dataArrayOf(index + 1, null);
        toUpdateArray[index] = element;
        return deepUpdate(dataList, createDataList(toUpdateArray), createDataList(operatorsArray));
    }

    @ZenMethod
    public static IData deepUpdate(IData data, IData toUpdate, @Optional IData updateOperation) {
        return DeepDataUpdater.deepUpdate(data, toUpdate, updateOperation);
    }

    private static IData[] dataArrayOf(int length, IData element) {
        IData[] dataArray = new IData[length];
        if (element != null) {
            Arrays.fill(dataArray, element);
        }
        return dataArray;
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
