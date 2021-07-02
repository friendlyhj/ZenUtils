package youyihj.zenutils.impl.network;

import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.mc1120.world.MCBlockPos;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.util.CrTUUID;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author youyihj
 */
public class ZenUtilsByteBuf implements IByteBuf {
    private final ByteBuf buf;

    public ZenUtilsByteBuf(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeInt(int value) {
        buf.writeInt(value);
    }

    @Override
    public void writeByte(byte value) {
        buf.writeByte(value);
    }

    @Override
    public void writeLong(long value) {
        buf.writeLong(value);
    }

    @Override
    public void writeFloat(float value) {
        buf.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) {
        buf.writeDouble(value);
    }

    @Override
    public void writeString(String string) {
        buf.writeInt(string.length());
        buf.writeCharSequence(string, StandardCharsets.UTF_8);
    }

    @Override
    public void writeItemStack(IItemStack itemStack) {
        if (itemStack.isEmpty()) {
            buf.writeInt(-1);
            return;
        }
        ItemStack mcStack = CraftTweakerMC.getItemStack(itemStack);
        this.writeString(mcStack.getItem().getRegistryName().toString());
        this.writeInt(mcStack.getCount());
        this.writeInt(mcStack.getItemDamage());
        this.writeData(itemStack.getTag());
    }

    @Override
    public void writeBlockPos(IBlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    @Override
    public void writeData(IData data) {
        ByteAndDataConverter.writeDataToBytes(this, data);
    }

    @Override
    public void writeUUID(CrTUUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public String readString() {
        int length = buf.readInt();
        return buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
    }

    @Override
    public IBlockPos readBlockPos() {
        return new MCBlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public IItemStack readItemStack() {
        if (this.readInt() == -1) {
            return null;
        }
        String name = this.readString();
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        if (item == null) {
            return null;
        }
        ItemStack mcStack = new ItemStack(item, this.readInt(), this.readInt());
        IItemStack itemStack = CraftTweakerMC.getIItemStack(mcStack);
        return itemStack.withTag(this.readData(), true);
    }

    @Override
    public IData readData() {
        return ByteAndDataConverter.readDataFromBytes(this);
    }

    @Override
    public CrTUUID readUUID() {
        return new CrTUUID(new UUID(buf.readLong(), buf.readLong()));
    }

    @Override
    public ByteBuf getInternal() {
        return buf;
    }
}
