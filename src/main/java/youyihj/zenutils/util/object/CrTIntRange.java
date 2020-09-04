package youyihj.zenutils.util.object;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;

@ZenRegister
@ZenClass("mods.zenutils.IntRange")
@SuppressWarnings("unused")
public class CrTIntRange {
    private IntRange intRange;

    private CrTIntRange(IntRange range) {
        this.intRange = range;
    }

    @ZenMethod
    public static CrTIntRange get(int from, int to) {
        return new CrTIntRange(new IntRange(from, to));
    }

    @ZenMethod
    public static CrTIntRange get(IntRange range) {
        return new CrTIntRange(range);
    }

    @ZenCaster
    @ZenMethod
    public IntRange castToIntRange() {
        return this.intRange;
    }

    @ZenMethod
    public int getFrom() {
        return this.intRange.getFrom();
    }

    @ZenMethod
    public int getTo() {
        return this.intRange.getTo();
    }
}
