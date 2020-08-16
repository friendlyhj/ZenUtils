package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.List;


@ZenRegister
@ZenClass("mods.zenutils.command.TabCompletionCase")
public class TabCompletionCase {

    static HashMap<String, List<String>> cases = new HashMap<>();

    @ZenMethod
    public static void add(String key, List<String> value) {
        cases.put(key, value);
    }
}
