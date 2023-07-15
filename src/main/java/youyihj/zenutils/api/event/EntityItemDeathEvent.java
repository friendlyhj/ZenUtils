package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.IEntityEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.event.EntityItemDeathEvent")
@ZenRegister
public class EntityItemDeathEvent implements IEntityEvent {
    private final EntityItem entityItem;
    private final DamageSource damageSource;

    public EntityItemDeathEvent(EntityItem entityItem, DamageSource damageSource) {
        this.entityItem = entityItem;
        this.damageSource = damageSource;
    }

    @ZenGetter("item")
    public IEntityItem getEntityItem() {
        return CraftTweakerMC.getIEntityItem(entityItem);
    }

    @ZenGetter("damageSource")
    public IDamageSource getDamageSource() {
        return CraftTweakerMC.getIDamageSource(damageSource);
    }

    @Override
    public IEntity getEntity() {
        return getEntityItem();
    }
}
