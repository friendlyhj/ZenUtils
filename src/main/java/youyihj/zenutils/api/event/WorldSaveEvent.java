package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IWorldEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import net.minecraftforge.event.world.WorldEvent;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.event.WorldSaveEvent")
@ZenRegister
public class WorldSaveEvent implements IWorldEvent {
    private final WorldEvent.Save event;


    public WorldSaveEvent(WorldEvent.Save event) {
        this.event = event;
    }

    @Override
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(event.getWorld());
    }
}
