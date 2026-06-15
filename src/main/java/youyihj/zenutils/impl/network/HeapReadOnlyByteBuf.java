package youyihj.zenutils.impl.network;

import java.nio.charset.StandardCharsets;

/**
 * @author youyihj
 */
public class HeapReadOnlyByteBuf extends AbstractByteBuf {
    private final byte[] data;
    private int cursor = 0;

    public HeapReadOnlyByteBuf(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean readBoolean() {
        return data[cursor++] != 0;
    }

    @Override
    public int readInt() {
        int value = 0;
        for (int shift = 0; shift < 5; shift++) {
            byte b = data[cursor++];
            value |= (b & 0x7F) << (shift * 7);
            if ((b & 0x80) == 0) {
                return value;
            }
        }
        throw new RuntimeException("VarInt is too big");
    }

    @Override
    public byte readByte() {
        return data[cursor++];
    }

    @Override
    public byte[] readBytes() {
        int length = readInt();
        byte[] result = new byte[length];
        System.arraycopy(data, cursor, result, 0, length);
        cursor += length;
        return result;
    }

    @Override
    public short readShort() {
        short value = (short) (((data[cursor] & 0xFF) << 8) | (data[cursor + 1] & 0xFF));
        cursor += 2;
        return value;
    }

    @Override
    public long readLong() {
        long value = 0;
        for (int shift = 0; shift < 10; shift++) {
            byte b = data[cursor++];
            value |= (long) (b & 0x7F) << (shift * 7);
            if ((b & 0x80) == 0) {
                return value;
            }
        }
        throw new RuntimeException("VarLong is too big");
    }

    @Override
    public float readFloat() {
        int bits = 0;
        bits |= ((data[cursor++] & 0xFF) << 24);
        bits |= ((data[cursor++] & 0xFF) << 16);
        bits |= ((data[cursor++] & 0xFF) << 8);
        bits |= ((data[cursor++] & 0xFF));
        return Float.intBitsToFloat(bits);
    }

    @Override
    public double readDouble() {
        long bits = 0;
        bits |= ((long) (data[cursor++] & 0xFF) << 56);
        bits |= ((long) (data[cursor++] & 0xFF) << 48);
        bits |= ((long) (data[cursor++] & 0xFF) << 40);
        bits |= ((long) (data[cursor++] & 0xFF) << 32);
        bits |= ((long) (data[cursor++] & 0xFF) << 24);
        bits |= ((long) (data[cursor++] & 0xFF) << 16);
        bits |= ((long) (data[cursor++] & 0xFF) << 8);
        bits |= ((long) (data[cursor++] & 0xFF));
        return Double.longBitsToDouble(bits);
    }

    @Override
    public String readString() {
        int length = readInt();
        String result = new String(data, cursor, length, StandardCharsets.UTF_8);
        cursor += length;
        return result;
    }

    public int readIndex() {
        return cursor;
    }

    @Override
    public void writeBoolean(boolean value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeInt(int value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeByte(byte value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeBytes(byte[] value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public short writeShort(short value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeLong(long value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeFloat(float value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeDouble(double value) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }

    @Override
    public void writeString(String string) {
        throw new UnsupportedOperationException("This bytebuf is read-only");
    }
}
