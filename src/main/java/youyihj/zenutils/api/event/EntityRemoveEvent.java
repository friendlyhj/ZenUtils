package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.IEntityEvent;
import crafttweaker.api.event.IWorldEvent;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.event.EntityRemoveEvent")
public class EntityRemoveEvent implements IEntityEvent, IWorldEvent {
    private final IEntity entity;
    private final IWorld world;

    public EntityRemoveEvent(IEntity entity, IWorld world) {
        this.entity = entity;
        this.world = world;
    }

    @Override
    public IEntity getEntity() {
        return entity;
    }

    @Override
    public IWorld getWorld() {
        return world;
    }
}
