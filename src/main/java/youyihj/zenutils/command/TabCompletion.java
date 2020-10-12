package youyihj.zenutils.command;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zenutils.command.TabCompletion")
@Deprecated
public class TabCompletion {
    private String[] info;

    private TabCompletion(String[] info) {
        this.info = info;
    }

    @ZenMethod
    public static TabCompletion create(String[] info) {
        return new TabCompletion(info);
    }

    @ZenMethod
    public String[] getInfo() {
        return this.info;
    }
}
