package youyihj.zenutils.api.cotx.function;

import crafttweaker.api.entity.IEntityItem;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@FunctionalInterface
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.IEntityItemUpdate")
public interface IEntityItemUpdate {
    boolean update(IEntityItem entityItem);
}
