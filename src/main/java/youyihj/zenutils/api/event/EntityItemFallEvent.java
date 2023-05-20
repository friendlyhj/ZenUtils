package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.event.EntityItemFallEvent")
public class EntityItemFallEvent {
    private final EntityItem entityItem;
    private final float distance;

    public EntityItemFallEvent(EntityItem entityItem, float distance) {
        this.entityItem = entityItem;
        this.distance = distance;
    }

    @ZenGetter("item")
    public IEntityItem getItem() {
        return CraftTweakerMC.getIEntityItem(entityItem);
    }

    @ZenGetter("distance")
    public float getDistance() {
        return distance;
    }
}
