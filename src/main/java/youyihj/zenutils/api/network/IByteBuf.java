package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import io.netty.buffer.ByteBuf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IByteBuf")
public interface IByteBuf {
    @ZenMethod
    void writeInt(int value);

    @ZenMethod
    void writeByte(byte value);

    @ZenMethod
    void writeLong(long value);

    @ZenMethod
    void writeFloat(float value);

    @ZenMethod
    void writeDouble(double value);

    @ZenMethod
    void writeString(String string);

    @ZenMethod
    void writeItemStack(IItemStack itemStack);

    @ZenMethod
    void writeData(IData data);

    @ZenMethod
    int readInt();

    @ZenMethod
    byte readByte();

    @ZenMethod
    long readLong();

    @ZenMethod
    float readFloat();

    @ZenMethod
    double readDouble();

    @ZenMethod
    String readString();

    @ZenMethod
    IItemStack readItemStack();

    @ZenMethod
    IData readData();

    ByteBuf getInternal();
}
