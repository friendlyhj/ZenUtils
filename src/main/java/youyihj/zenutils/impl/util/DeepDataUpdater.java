package youyihj.zenutils.impl.util;

import crafttweaker.api.data.*;
import org.apache.commons.lang3.ArrayUtils;
import youyihj.zenutils.api.util.ExpandData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author youyihj
 */
public class DeepDataUpdater implements IDataConverter<IData> {
    public static class Operation {
        public static final int OVERWRITE = 0;
        public static final int APPEND = 1;
        public static final int MERGE = 2;
        public static final int REMOVE = 3;
        public static final int BUMP = 4;

        private static final int SUB_OPERATOR_MASK = 3;
    }

    @Nullable
    private final IData data;
    @Nullable
    private final IData updateOperation;

    public DeepDataUpdater(IData data, IData updateOperation) {
        this.data = data;
        this.updateOperation = updateOperation;
    }

    public static IData deepUpdate(@Nullable IData data, @Nonnull IData toUpdate, @Nullable IData updateOperation) {
        return toUpdate.convert(new DeepDataUpdater(data, updateOperation));
    }

    @Override
    public IData fromBool(boolean value) {
        return new DataBool(value);
    }

    @Override
    public IData fromByte(byte value) {
        return new DataByte(value);
    }

    @Override
    public IData fromShort(short value) {
        return new DataShort(value);
    }

    @Override
    public IData fromInt(int value) {
        return new DataInt(value);
    }

    @Override
    public IData fromLong(long value) {
        return new DataLong(value);
    }

    @Override
    public IData fromFloat(float value) {
        return new DataFloat(value);
    }

    @Override
    public IData fromDouble(double value) {
        return new DataDouble(value);
    }

    @Override
    public IData fromString(String value) {
        return new DataString(value);
    }

    @Override
    public IData fromList(List<IData> values) {
        List<IData> oldValues = Optional.ofNullable(data).map(IData::asList).orElse(Collections.emptyList());
        List<IData> newValues = new ArrayList<>(oldValues);
        if (updateOperation == null) {
            return new DataList(values, true);
        }
        if (!isCollection(updateOperation)) {
            int operator = updateOperation.asInt();
            if ((operator & Operation.BUMP) != 0) {
                IData toUpdate = new DataList(values, true);
                switch (operator & Operation.SUB_OPERATOR_MASK) {
                    case Operation.APPEND:
                        newValues.add(toUpdate);
                        break;
                    case Operation.MERGE:
                        boolean contains = false;
                        for (IData oldValue : oldValues) {
                            if (safeEquals(toUpdate, oldValue)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            newValues.add(toUpdate);
                        }
                        break;
                    case Operation.REMOVE:
                        newValues.removeIf(it -> safeEquals(toUpdate, it));
                        break;
                    default:
                        newValues = Collections.singletonList(toUpdate);
                        break;
                }
            } else {
                switch (operator & Operation.SUB_OPERATOR_MASK) {
                    case Operation.OVERWRITE:
                        newValues = values;
                        break;
                    case Operation.APPEND:
                        newValues.addAll(values);
                        break;
                    case Operation.MERGE:
                        for (IData value : values) {
                            if (!safeContains(oldValues, value)) {
                                newValues.add(value);
                            }
                        }
                        break;
                    case Operation.REMOVE:
                        if (values.isEmpty()) {
                            newValues.clear();
                        } else {
                            newValues.removeIf(it -> safeContains(values, it));
                        }
                        break;
                }
            }
        } else {
            List<IData> operationsList = updateOperation.asList();
            IData operator = ExpandData.DataUpdateOperation.OVERWRITE;
            for (int i = 0; i < values.size(); i++) {
                if (i < operationsList.size()) {
                    operator = operationsList.get(i);
                }
                IData newValue = values.get(i);
                if (newValue != null) {
                    if (i < oldValues.size()) {
                        IData oldValue = oldValues.get(i);
                        newValues.set(i, deepUpdate(oldValue, newValue, operator));
                    } else if ((operator.asInt() & Operation.SUB_OPERATOR_MASK) != Operation.REMOVE) {
                        newValues.add(newValue);
                    }
                }
            }
        }
        return new DataList(newValues, true);
    }

    @Override
    public IData fromMap(Map<String, IData> values) {
        if (updateOperation == null || !isMap(data)) {
            return new DataMap(values, true);
        }
        Map<String, IData> dataMap = new HashMap<>(data.asMap());
        if (isMap(updateOperation)) {
            values.forEach((key, value) ->
                dataMap.compute(key, (k, oldValue) -> deepUpdate(oldValue, value, updateOperation.memberGet(k)))
            );
        } else {
            int operator = updateOperation.asInt();
            switch (operator & Operation.SUB_OPERATOR_MASK) {
                case Operation.OVERWRITE:
                    dataMap.clear();
                    dataMap.putAll(values);
                    break;
                case Operation.APPEND:
                case Operation.MERGE:
                    values.forEach((key, value) ->
                        dataMap.compute(key, (k, oldValue) -> deepUpdate(oldValue, value, new DataInt(operator)))
                    );
                    break;
                case Operation.REMOVE:
                    if (values.isEmpty()) {
                        dataMap.clear();
                    }
                    for (String s : values.keySet()) {
                        dataMap.remove(s);
                    }
                    break;
            }
        }
        return new DataMap(dataMap, true);
    }

    @Override
    public IData fromByteArray(byte[] value) {
        if (data == null || updateOperation == null) {
            return new DataByteArray(value, true);
        }
        if (data instanceof DataList) {
            return fromList(new DataByteArray(value, true).asList());
        }
        byte[] origin = data.asByteArray();
        if (origin == null) {
            return new DataByteArray(value, true);
        }
        switch (updateOperation.asInt() & Operation.SUB_OPERATOR_MASK) {
            case Operation.OVERWRITE:
                return new DataByteArray(value, true);
            case Operation.APPEND:
                return new DataByteArray(ArrayUtils.addAll(origin, value), true);
            case Operation.MERGE:
                return new DataByteArray(mergeByteArrays(origin, value), true);
            case Operation.REMOVE:
                return new DataByteArray(value.length == 0 ? value : removeByteArrays(origin, value), true);
        }
        return new DataByteArray(value, true);
    }

    @Override
    public IData fromIntArray(int[] value) {
        if (data == null || updateOperation == null) {
            return new DataIntArray(value, true);
        }
        if (data instanceof DataList) {
            return fromList(new DataIntArray(value, true).asList());
        }
        int[] origin = data.asIntArray();
        if (origin == null) {
            return new DataIntArray(value, true);
        }
        switch (updateOperation.asInt() & Operation.SUB_OPERATOR_MASK) {
            case Operation.OVERWRITE:
                return new DataIntArray(value, true);
            case Operation.APPEND:
                return new DataIntArray(ArrayUtils.addAll(origin, value), true);
            case Operation.MERGE:
                return new DataIntArray(mergeIntArrays(origin, value), true);
            case Operation.REMOVE:
                return new DataIntArray(value.length == 0 ? value : removeIntArrays(origin, value), true);
        }
        return new DataIntArray(value, true);
    }

    private static boolean isCollection(IData data) {
        return (data != null) && (data instanceof DataList || data instanceof DataIntArray || data instanceof DataByteArray || data.asList() != null);
    }

    private static boolean isMap(IData data) {
        return data instanceof DataMap;
    }

    // can not replace to Objects.equals, we need to call IData parameter overload
    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    private static boolean safeEquals(IData a, IData b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private static boolean safeContains(List<IData> data, IData element) {
        for (IData datum : data) {
            if (safeEquals(datum, element)) {
                return true;
            }
        }
        return false;
    }

    // merge and remove array functions

    private static int[] mergeIntArrays(int[] a, int[] b) {
        boolean binarySearch = shouldBinarySearch(a.length, b.length);
        int[] checkArray = a;
        if (binarySearch) {
            checkArray = a.clone();
            Arrays.sort(checkArray);
        }
        int[] result = Arrays.copyOf(a, a.length + b.length);
        int addedIndex = a.length;
        for (int i : b) {
            if ((binarySearch ? Arrays.binarySearch(checkArray, i) : ArrayUtils.indexOf(checkArray, i)) != ArrayUtils.INDEX_NOT_FOUND) {
                result[addedIndex++] = i;
            }
        }
        return Arrays.copyOf(result, addedIndex);
    }

    private static byte[] mergeByteArrays(byte[] a, byte[] b) {
        boolean binarySearch = shouldBinarySearch(a.length, b.length);
        byte[] checkArray = a;
        if (binarySearch) {
            checkArray = a.clone();
            Arrays.sort(checkArray);
        }
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        int addedIndex = a.length;
        for (byte i : b) {
            if ((binarySearch ? Arrays.binarySearch(checkArray, i) : ArrayUtils.indexOf(checkArray, i)) != ArrayUtils.INDEX_NOT_FOUND) {
                result[addedIndex++] = i;
            }
        }
        return Arrays.copyOf(result, addedIndex);
    }

    private static int[] removeIntArrays(int[] array, int[] toRemove) {
        boolean binarySearch = shouldBinarySearch(array.length, toRemove.length);
        int[] checkArray = array;
        if (binarySearch) {
            checkArray = array.clone();
            Arrays.sort(checkArray);
        }
        int[] result = new int[array.length];
        int addedIndex = 0;
        for (int i : array) {
            if ((binarySearch ? Arrays.binarySearch(checkArray, i) : ArrayUtils.indexOf(checkArray, i)) != ArrayUtils.INDEX_NOT_FOUND) {
                result[addedIndex++] = i;
            }
        }
        return Arrays.copyOf(result, addedIndex);
    }

    private static byte[] removeByteArrays(byte[] array, byte[] toRemove) {
        boolean binarySearch = shouldBinarySearch(array.length, toRemove.length);
        byte[] checkArray = array;
        if (binarySearch) {
            checkArray = array.clone();
            Arrays.sort(checkArray);
        }
        byte[] result = new byte[array.length];
        int addedIndex = 0;
        for (byte i : array) {
            if ((binarySearch ? Arrays.binarySearch(checkArray, i) : ArrayUtils.indexOf(checkArray, i)) != ArrayUtils.INDEX_NOT_FOUND) {
                result[addedIndex++] = i;
            }
        }
        return Arrays.copyOf(result, addedIndex);
    }

    private static boolean shouldBinarySearch(int total, int items) {
        return items > Math.max(2, Integer.highestOneBit(total));
    }
}
