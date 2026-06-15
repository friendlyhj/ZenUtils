package youyihj.zenutils.impl.network;

import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.mc1120.world.MCBlockPos;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import youyihj.zenutils.api.network.IByteBuf;
import youyihj.zenutils.api.util.CrTUUID;

import java.util.UUID;

/**
 * @author youyihj
 */
public abstract class AbstractByteBuf implements IByteBuf {
    @Override
    public void writeItemStack(IItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            this.writeBoolean(true);
            return;
        } else {
            this.writeBoolean(false);
        }
        ItemStack mcStack = CraftTweakerMC.getItemStack(itemStack);
        this.writeInt(Item.getIdFromItem(mcStack.getItem()));
        this.writeInt(mcStack.getCount());
        this.writeInt(mcStack.getItemDamage());
        if (itemStack.hasTag()) {
            this.writeData(itemStack.getTag());
        } else {
            this.writeData(null);
        }
    }

    @Override
    public void writeBlockPos(IBlockPos pos) {
        this.writeInt(pos.getX());
        this.writeInt(pos.getY());
        this.writeInt(pos.getZ());
    }

    @Override
    public void writeData(IData data) {
        ByteAndDataConverter.writeDataToBytes(this, data);
    }

    @Override
    public void writeUUID(CrTUUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public IBlockPos readBlockPos() {
        return new MCBlockPos(this.readInt(), this.readInt(), this.readInt());
    }

    @Override
    public IItemStack readItemStack() {
        if (this.readBoolean()) {
            return null;
        }
        Item item = Item.getItemById(this.readInt());
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
        return new CrTUUID(new UUID(this.readLong(), this.readLong()));
    }
}
