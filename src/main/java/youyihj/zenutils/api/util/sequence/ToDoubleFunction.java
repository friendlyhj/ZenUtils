package youyihj.zenutils.api.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.sequence.ToDoubleFunction")
public interface ToDoubleFunction<T> {
    int toDouble(T t);
}
