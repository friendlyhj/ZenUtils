package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IWorldCondition")
@FunctionalInterface
public interface IWorldCondition {
    @ZenMethod
    boolean apply(IWorld world, CatenationContext context);
}
