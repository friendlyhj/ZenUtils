package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;

@ZenRegister
@ZenClass("mods.zenutils.IntRange")
@SuppressWarnings("unused")
public class CTIntRange {
    private IntRange intRange;

    private CTIntRange(IntRange range) {
        this.intRange = range;
    }

    @ZenMethod
    public static CTIntRange get(int from, int to) {
        return new CTIntRange(new IntRange(from, to));
    }

    @ZenMethod
    public static CTIntRange get(IntRange range) {
        return new CTIntRange(range);
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
