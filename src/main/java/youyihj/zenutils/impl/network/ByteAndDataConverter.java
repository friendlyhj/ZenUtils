package youyihj.zenutils.impl.network;

import crafttweaker.api.data.*;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import youyihj.zenutils.api.network.IByteBuf;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author youyihj
 */
public class ByteAndDataConverter {
    private static final Map<Class<? extends IData>, IDataWriter<?>> WRITERS = new HashMap<>();
    private static final Byte2ObjectArrayMap<IDataReader<?>> READERS = new Byte2ObjectArrayMap<>();
    private static final Map<Class<? extends IData>, Byte> CLASS_BYTE_MAP = new HashMap<>();
    private static final byte NULL_DATA_ID = 15;

    @SuppressWarnings("unchecked")
    public static void writeDataToBytes(IByteBuf byteBuf, @Nullable IData data) {
        IDataWriter<IData> dataWriter;
        if (data == null) {
            dataWriter = IDataWriter.EMPTY;
        } else {
            dataWriter = (IDataWriter<IData>) WRITERS.get(data.getClass());
            byteBuf.writeByte(CLASS_BYTE_MAP.get(data.getClass()));
        }
        dataWriter.write(byteBuf, data);
    }

    @Nullable
    public static IData readDataFromBytes(IByteBuf byteBuf) {
        byte id = byteBuf.readByte();
        return READERS.get(id).read(byteBuf);
    }

    @FunctionalInterface
    private interface IDataWriter<T extends IData> {
        IDataWriter<IData> EMPTY = ((byteBuf, data) -> byteBuf.writeByte(NULL_DATA_ID));

        void write(IByteBuf byteBuf, T data);
    }

    @FunctionalInterface
    private interface IDataReader<T extends IData> {
        IDataReader<IData> EMPTY = (byteBuf) -> null;

        T read(IByteBuf byteBuf);
    }

    private static <T extends IData> void addWriter(Class<T> type, IDataWriter<T> writer) {
        WRITERS.put(type, writer);
    }

    private static <T extends IData> void addReader(int id, Class<T> type, IDataReader<T> reader) {
        READERS.put(((byte) id), reader);
        CLASS_BYTE_MAP.put(type, ((byte) id));
    }

    static {
        // DataBool ID 0
        addWriter(DataBool.class, ByteAndDataConverter::writeBool);
        addReader(0, DataBool.class, ByteAndDataConverter::readBool);
        // DataByte ID 1
        addWriter(DataByte.class, ByteAndDataConverter::writeByte);
        addReader(1, DataByte.class, ByteAndDataConverter::readByte);
        // DataByteArray ID 2
        addWriter(DataByteArray.class, ByteAndDataConverter::writeByteArray);
        addReader(2, DataByteArray.class, ByteAndDataConverter::readByteArray);
        // DataDouble ID 3
        addWriter(DataDouble.class, ByteAndDataConverter::writeDouble);
        addReader(3, DataDouble.class, ByteAndDataConverter::readDouble);
        // DataFloat ID 4
        addWriter(DataFloat.class, ByteAndDataConverter::writeFloat);
        addReader(4, DataFloat.class, ByteAndDataConverter::readFloat);
        // DataInt ID 5
        addWriter(DataInt.class, ByteAndDataConverter::writeInt);
        addReader(5, DataInt.class, ByteAndDataConverter::readInt);
        // DataIntArray ID 6
        addWriter(DataIntArray.class, ByteAndDataConverter::writeIntArray);
        addReader(6, DataIntArray.class, ByteAndDataConverter::readIntArray);
        // DataList ID 7
        addWriter(DataList.class, ByteAndDataConverter::writeList);
        addReader(7, DataList.class, ByteAndDataConverter::readList);
        // DataLong ID 8
        addWriter(DataLong.class, ByteAndDataConverter::writeLong);
        addReader(8, DataLong.class, ByteAndDataConverter::readLong);
        // DataShort ID 9
        addWriter(DataShort.class, ByteAndDataConverter::writeShort);
        addReader(9, DataShort.class, ByteAndDataConverter::readShort);
        // DataString ID 10
        addWriter(DataString.class, ByteAndDataConverter::writeString);
        addReader(10, DataString.class, ByteAndDataConverter::readString);
        // DataMap ID 11
        addWriter(DataMap.class, ByteAndDataConverter::writeMap);
        addReader(11, DataMap.class, ByteAndDataConverter::readMap);
        // EMPTY ID 15
        addWriter(IData.class, IDataWriter.EMPTY);
        addReader(NULL_DATA_ID, IData.class, IDataReader.EMPTY);
    }

    private static void writeBool(IByteBuf byteBuf, DataBool bool) {
        byteBuf.writeByte(bool.asByte());
    }

    private static DataBool readBool(IByteBuf byteBuf) {
        return new DataBool(byteBuf.readByte() == 1);
    }

    private static void writeByte(IByteBuf byteBuf, DataByte dataByte) {
        byteBuf.writeByte(dataByte.asByte());
    }

    private static DataByte readByte(IByteBuf byteBuf) {
        return new DataByte(byteBuf.readByte());
    }

    private static void writeByteArray(IByteBuf byteBuf, DataByteArray byteArray) {
        byte[] bytes = byteArray.asByteArray();
        byteBuf.writeInt(bytes.length);
        byteBuf.getInternal().writeBytes(bytes);
    }

    private static DataByteArray readByteArray(IByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readInt()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteBuf.readByte();
        }
        return new DataByteArray(bytes, true);
    }

    private static void writeDouble(IByteBuf byteBuf, DataDouble dataDouble) {
        byteBuf.writeDouble(dataDouble.asDouble());
    }

    private static DataDouble readDouble(IByteBuf byteBuf) {
        return new DataDouble(byteBuf.readDouble());
    }

    private static void writeFloat(IByteBuf byteBuf, DataFloat dataFloat) {
        byteBuf.writeFloat(dataFloat.asFloat());
    }

    private static DataFloat readFloat(IByteBuf byteBuf) {
        return new DataFloat(byteBuf.readFloat());
    }

    private static void writeInt(IByteBuf byteBuf, DataInt dataInt) {
        byteBuf.writeInt(dataInt.asInt());
    }

    private static DataInt readInt(IByteBuf byteBuf) {
        return new DataInt(byteBuf.readInt());
    }

    private static void writeIntArray(IByteBuf byteBuf, DataIntArray intArray) {
        int[] ints = intArray.asIntArray();
        byteBuf.writeInt(ints.length);
        for (int i : ints) {
            byteBuf.writeInt(i);
        }
    }

    private static DataIntArray readIntArray(IByteBuf byteBuf) {
        int[] ints = new int[byteBuf.readInt()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = byteBuf.readInt();
        }
        return new DataIntArray(ints, true);
    }

    private static void writeList(IByteBuf byteBuf, DataList dataList) {
        List<IData> list = dataList.asList();
        byteBuf.writeInt(list.size());
        list.forEach(byteBuf::writeData);
    }

    private static DataList readList(IByteBuf byteBuf) {
        return new DataList(Stream.generate(byteBuf::readData).limit(byteBuf.readInt()).collect(Collectors.toList()), true);
    }

    private static void writeLong(IByteBuf byteBuf, DataLong dataLong) {
        byteBuf.writeLong(dataLong.asLong());
    }

    private static DataLong readLong(IByteBuf byteBuf) {
        return new DataLong(byteBuf.readLong());
    }

    private static void writeShort(IByteBuf byteBuf, DataShort dataShort) {
        byteBuf.getInternal().writeShort(dataShort.asShort());
    }

    private static DataShort readShort(IByteBuf byteBuf) {
        return new DataShort(byteBuf.getInternal().readShort());
    }

    private static void writeString(IByteBuf byteBuf, DataString dataString) {
        byteBuf.writeString(dataString.asString());
    }

    private static DataString readString(IByteBuf byteBuf) {
        return new DataString(byteBuf.readString());
    }

    private static void writeMap(IByteBuf byteBuf, DataMap dataMap) {
        Map<String, IData> stringIDataMap = dataMap.asMap();
        byteBuf.writeInt(stringIDataMap.size());
        stringIDataMap.forEach((key, value) -> {
            byteBuf.writeString(key);
            byteBuf.writeData(value);
        });
    }

    private static DataMap readMap(IByteBuf byteBuf) {
        Map<String, IData> temp = new HashMap<>();
        int bound = byteBuf.readInt();
        for (int i = 0; i < bound; i++) {
            temp.put(byteBuf.readString(), byteBuf.readData());
        }
        return new DataMap(temp, true);
    }
}
