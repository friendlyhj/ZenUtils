package youyihj.zenutils.api.network;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.world.IBlockPos;
import io.netty.buffer.ByteBuf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.CrTUUID;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IByteBuf")
public interface IByteBuf {
    @ZenMethod
    void writeBoolean(boolean value);

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
    void writeBlockPos(IBlockPos pos);

    @ZenMethod
    void writeItemStack(IItemStack itemStack);

    @ZenMethod
    void writeData(IData data);

    @ZenMethod
    void writeUUID(CrTUUID uuid);

    @ZenMethod
    boolean readBoolean();

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
    IBlockPos readBlockPos();

    @ZenMethod
    IItemStack readItemStack();

    @ZenMethod
    IData readData();

    @ZenMethod
    CrTUUID readUUID();

    ByteBuf getInternal();
}
