package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.IWorldFunction")
@FunctionalInterface
public interface IWorldFunction {
    @ZenMethod
    void apply(IWorld world, CatenationContext context);
}
