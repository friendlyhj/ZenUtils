package youyihj.zenutils.api.cotx.function;

import crafttweaker.api.entity.IEntityItem;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@FunctionalInterface
@ZenClass("mods.zenutils.cotx.IEntityItemUpdate")
public interface IEntityItemUpdate {
    boolean update(IEntityItem entityItem);
}
