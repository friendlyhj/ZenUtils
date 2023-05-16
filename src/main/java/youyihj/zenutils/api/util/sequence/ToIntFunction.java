package youyihj.zenutils.api.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.sequence.ToIntFunction")
public interface ToIntFunction<T> {
    int toInt(T t);
}
