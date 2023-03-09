package youyihj.zenutils.impl.util.catenation.persistence;

import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.mc1120.world.MCBlockPos;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

/**
 * @author youyihj
 */
public class BlockPosObjectHolder implements ICatenationObjectHolder<IBlockPos> {
    private IBlockPos pos;

    @Override
    public Type<IBlockPos> getType() {
        return BuiltinObjectHolderTypes.POSITION;
    }

    @Override
    public IData serializeToData() {
        return new DataIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}, true);
    }

    @Override
    public void deserializeFromData(IData data) {
        int[] ints = data.asIntArray();
        this.pos = new MCBlockPos(ints[0], ints[1], ints[2]);
    }

    @Override
    public void receiveObject(IBlockPos object) {
        if (this.pos != null) {
            this.pos = object;
        }
    }

    @Override
    public IBlockPos getValue() {
        return pos;
    }

    @Override
    public void setValue(IBlockPos value) {
        this.pos = value;
    }

    @Override
    public boolean isReady(Catenation catenation) {
        return CraftTweakerMC.getWorld(catenation.getWorld()).isBlockLoaded(CraftTweakerMC.getBlockPos(pos));
    }
}
