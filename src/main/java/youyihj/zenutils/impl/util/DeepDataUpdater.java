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
            values.forEach((key, value) -> {
                IData oldValue = dataMap.get(key);
                dataMap.put(key, deepUpdate(oldValue, value, updateOperation.memberGet(key)));
            });
        } else {
            int operator = updateOperation.asInt();
            switch (operator & Operation.SUB_OPERATOR_MASK) {
                case Operation.OVERWRITE:
                    dataMap.clear();
                    dataMap.putAll(values);
                    break;
                case Operation.APPEND:
                case Operation.MERGE:
                    values.forEach((key, value) -> {
                        IData oldValue = dataMap.get(key);
                        dataMap.put(key, deepUpdate(oldValue, value, new DataInt(operator)));
                    });
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
                byte[] originSorted = origin.clone();
                Arrays.sort(originSorted);
                byte[] temp = Arrays.copyOf(origin, origin.length + value.length);
                int addCount = 0;
                for (byte b : value) {
                    if (Arrays.binarySearch(originSorted, b) < 0) {
                        temp[origin.length + addCount] = b;
                        addCount++;
                    }
                }
                return new DataByteArray(Arrays.copyOf(temp, origin.length + addCount), true);
            case Operation.REMOVE:
                byte[] valueSorted = value.clone();
                Arrays.sort(valueSorted);
                byte[] tempR = new byte[origin.length];
                int addCountR = 0;
                for (byte b : origin) {
                    if (Arrays.binarySearch(valueSorted, b) < 0) {
                        tempR[addCountR] = b;
                        addCountR++;
                    }
                }
                return new DataByteArray(Arrays.copyOf(tempR, addCountR), true);
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
                int[] originSorted = origin.clone();
                Arrays.sort(originSorted);
                int[] temp = Arrays.copyOf(origin, origin.length + value.length);
                int addCount = 0;
                for (int i : value) {
                    if (Arrays.binarySearch(originSorted, i) < 0) {
                        temp[origin.length + addCount] = i;
                        addCount++;
                    }
                }
                return new DataIntArray(Arrays.copyOf(temp, origin.length + addCount), true);
            case Operation.REMOVE:
                int[] valueSorted = value.clone();
                Arrays.sort(valueSorted);
                int[] tempR = new int[origin.length];
                int addCountR = 0;
                for (int i : origin) {
                    if (Arrays.binarySearch(valueSorted, i) < 0) {
                        tempR[addCountR] = i;
                        addCountR++;
                    }
                }
                return new DataIntArray(Arrays.copyOf(tempR, addCountR), true);
        }
        return new DataIntArray(value, true);
    }

    private static boolean isCollection(IData data) {
        return (data != null) && (data instanceof DataList || data instanceof DataIntArray || data instanceof DataByteArray || data.asList() != null);
    }

    private static boolean isMap(IData data) {
        return data instanceof DataMap;
    }

    private static boolean safeEquals(IData a, IData b) {
        if (Objects.equals(a, b)) {
            return true;
        }
        if (isCollection(a)) {
            return isCollection(b) && a.equals(b);
        }
        if (isMap(a)) {
            return isMap(b) && a.equals(b);
        }
        return a.equals(b);
    }

    private static boolean safeContains(List<IData> data, IData element) {
        for (IData datum : data) {
            if (safeEquals(datum, element)) {
                return true;
            }
        }
        return false;
    }
}
