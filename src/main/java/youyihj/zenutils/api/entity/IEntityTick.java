package youyihj.zenutils.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@FunctionalInterface
@ZenClass("mods.zenutils.IEntityTick")
public interface IEntityTick {
    void tick(IEntity entity);
}
