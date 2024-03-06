package youyihj.zenutils.api.entity;

import com.google.common.base.Predicate;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IEntityPredicate")
public interface IEntityPredicate extends Predicate<IEntity> {
    @ZenMethod
    boolean test(IEntity entity);
}
