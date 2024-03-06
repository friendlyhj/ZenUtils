package youyihj.zenutils.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.INearbyEntityList")
public interface INearbyEntityList {
    @ZenMethod
    INearbyEntityList filterType(IEntityDefinition definition);

    @ZenMethod
    INearbyEntityList filter(IEntityPredicate predicate);

    @ZenMethod
    @ZenGetter
    List<IEntity> entities();

    @ZenMethod
    @ZenGetter
    List<IEntityLivingBase> livings();

    @ZenMethod
    @ZenGetter
    List<IPlayer> players();

    @ZenMethod
    @ZenGetter
    List<IEntityItem> items();

    @ZenMethod
    @ZenGetter
    IEntity closestEntity();

    @ZenMethod
    @ZenGetter
    IEntityLivingBase closestLiving();

    @ZenMethod
    @ZenGetter
    IPlayer closestPlayer();

    @ZenMethod
    @ZenGetter
    IEntityItem closestItem();
}
