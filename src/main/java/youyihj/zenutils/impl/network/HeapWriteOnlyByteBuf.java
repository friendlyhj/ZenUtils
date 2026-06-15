package youyihj.zenutils.impl.network;

import java.nio.charset.StandardCharsets;

/**
 * @author youyihj
 */
public class HeapWriteOnlyByteBuf extends AbstractByteBuf {
    private static final int DEFAULT_CAPACITY = 128;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private byte[] data;
    private int cursor = 0;

    public HeapWriteOnlyByteBuf(int capacity) {
        this.data = new byte[capacity];
    }

    public HeapWriteOnlyByteBuf() {
        this(DEFAULT_CAPACITY);
    }

    @Override
    public void writeBoolean(boolean value) {
        alloc(1);
        data[cursor++] = (byte) (value ? 1 : 0);
    }

    @Override
    public void writeInt(int value) {
        while ((value & -128) != 0)
        {
            this.writeByte((value & 127 | 128));
            value >>>= 7;
        }

        this.writeByte(value);
    }

    @Override
    public void writeByte(byte value) {
        alloc(1);
        data[cursor++] = value;
    }

    @Override
    public void writeBytes(byte[] value) {
        writeInt(value.length);
        alloc(value.length);
        System.arraycopy(value, 0, data, cursor, value.length);
        cursor += value.length;
    }

    @Override
    public short writeShort(short value) {
        alloc(2);
        data[cursor++] = (byte) ((value >> 8) & 0xFF);
        data[cursor++] = (byte) (value & 0xFF);
        return value;
    }

    @Override
    public void writeLong(long value) {
        while ((value & -128L) != 0L)
        {
            this.writeByte((int)(value & 127L) | 128);
            value >>>= 7;
        }

        this.writeByte((int)value);
    }

    @Override
    public void writeFloat(float value) {
        int bits = Float.floatToIntBits(value);
        alloc(4);
        data[cursor++] = (byte) ((bits >> 24) & 0xFF);
        data[cursor++] = (byte) ((bits >> 16) & 0xFF);
        data[cursor++] = (byte) ((bits >> 8) & 0xFF);
        data[cursor++] = (byte) (bits & 0xFF);
    }

    @Override
    public void writeDouble(double value) {
        long bits = Double.doubleToLongBits(value);
        alloc(8);
        data[cursor++] = (byte) ((bits >> 56) & 0xFF);
        data[cursor++] = (byte) ((bits >> 48) & 0xFF);
        data[cursor++] = (byte) ((bits >> 40) & 0xFF);
        data[cursor++] = (byte) ((bits >> 32) & 0xFF);
        data[cursor++] = (byte) ((bits >> 24) & 0xFF);
        data[cursor++] = (byte) ((bits >> 16) & 0xFF);
        data[cursor++] = (byte) ((bits >> 8) & 0xFF);
        data[cursor++] = (byte) (bits & 0xFF);
    }

    @Override
    public void writeString(String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        writeInt(stringBytes.length);
        alloc(stringBytes.length);
        System.arraycopy(stringBytes, 0, data, cursor, stringBytes.length);
        cursor += stringBytes.length;
    }

    public int writeIndex() {
        return cursor;
    }

    public byte[] getData() {
        return data;
    }

    private void alloc(int addition) {
        if (cursor + addition > data.length) {
            byte[] newData = new byte[(int) (data.length + data.length * DEFAULT_LOAD_FACTOR)];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
    }

    // discard 24 higher bits
    private void writeByte(int value) {
        this.writeByte((byte) (value & 0xFF));
    }

    @Override
    public boolean readBoolean() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public int readInt() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public byte readByte() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public byte[] readBytes() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public short readShort() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public long readLong() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public float readFloat() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public double readDouble() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }

    @Override
    public String readString() {
        throw new UnsupportedOperationException("This bytebuf is write-only");
    }
}
