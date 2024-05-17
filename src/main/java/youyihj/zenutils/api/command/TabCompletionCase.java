package youyihj.zenutils.api.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.command.TabCompletionCase")
@Deprecated
public class TabCompletionCase {

    static final HashMap<String, List<String>> cases = new HashMap<>();

    @ZenMethod
    public static void add(String key, List<String> value) {
        cases.put(key, value);
    }
}
