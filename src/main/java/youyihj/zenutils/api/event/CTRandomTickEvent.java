package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.IBlockEvent;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.event.RandomTickEvent")
public class CTRandomTickEvent implements IBlockEvent, IEventCancelable {
    private final RandomTickEvent event;

    public CTRandomTickEvent(RandomTickEvent event) {
        this.event = event;
    }

    @Override
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(event.getWorld());
    }

    @Override
    public IBlockState getBlockState() {
        return CraftTweakerMC.getBlockState(event.getState());
    }

    @Override
    public IBlockPos getPosition() {
        return CraftTweakerMC.getIBlockPos(event.getPos());
    }

    @Override
    public boolean isCanceled() {
        return event.isCanceled();
    }

    @Override
    public void setCanceled(boolean canceled) {
        event.setCanceled(canceled);
    }
}
