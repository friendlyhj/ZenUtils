package youyihj.zenutils.api.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.sequence.BiOperator")
public interface BiOperator<T> {
    T apply(T t1, T t2);
}
