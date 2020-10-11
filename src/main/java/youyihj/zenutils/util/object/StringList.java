package youyihj.zenutils.util.object;

import com.google.common.base.Functions;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.zenutils.StringList")
public class StringList {
    private List<String> inner;

    private StringList(Collection<?> list) {
        this.inner = list.stream().map(Functions.toStringFunction()).collect(Collectors.toList());
    }

    @ZenMethod
    public static StringList create(Collection<?> list) {
        return new StringList(list);
    }

    @ZenMethod
    public static StringList create(Object[] objects) {
        return new StringList(Arrays.asList(objects));
    }

    @ZenMethod
    public static StringList empty() {
        return new StringList(Collections.emptyList());
    }

    @ZenMethod
    public static StringList singletonList(Object object) {
        return new StringList(Collections.singletonList(object));
    }

    public List<String> getInner() {
        return inner;
    }
}
