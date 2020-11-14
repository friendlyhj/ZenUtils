package youyihj.zenutils.cotx.tile;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.cotx.INBTSerializable;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.cotx.TileEntity")
@ZenRegister
@ModOnly("contenttweaker")
public class CoTTileEntity extends TileEntity implements INBTSerializable, ITickable {
    private TileData customData;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        customData.writeToNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        customData.readFromNBT(compound);
        super.readFromNBT(compound);
    }

    @ZenMethod
    public IData getData() {
        return NBTConverter.from(this.writeToNBT(new NBTTagCompound()), true);
    }

    @ZenMethod
    public IData getCustomData() {
        return customData.getData();
    }

    @Override
    public void tick() {

    }
}
