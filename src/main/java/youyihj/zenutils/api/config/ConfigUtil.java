package youyihj.zenutils.api.config;

import crafttweaker.annotations.ZenRegister;
import youyihj.zenutils.api.config.elements.ConfigGroup;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("youyihj.zenutils.config.ConfigUtil")
public class ConfigUtil {
    @ZenMethod
    public static ConfigGroup named(String name) {
        return new ConfigGroup(null, name);
    }
}
