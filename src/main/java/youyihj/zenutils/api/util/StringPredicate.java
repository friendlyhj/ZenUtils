package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.function.Predicate;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.StringPredicate")
public interface StringPredicate extends Predicate<String> {
}
