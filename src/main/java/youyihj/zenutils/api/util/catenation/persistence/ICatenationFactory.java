package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.util.catenation.Catenation;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ICatenationFactory")
@FunctionalInterface
public interface ICatenationFactory {
    Catenation get(IWorld world);
}
