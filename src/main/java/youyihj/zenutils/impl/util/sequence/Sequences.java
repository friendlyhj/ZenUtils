package youyihj.zenutils.impl.util.sequence;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.api.util.sequence.Sequence;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("mods.zenutils.sequence.Sequence")
public class Sequences {
    @ZenMethodStatic
    public static <T> Sequence<T> fromList(List<T> list) {
        return new ListSequence<>(list);
    }
}
