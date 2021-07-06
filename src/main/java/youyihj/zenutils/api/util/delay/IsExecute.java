package youyihj.zenutils.api.util.delay;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.zenutils.isExecute")
public class IsExecute {

    @ZenProperty
    private boolean exec;

    public IsExecute(boolean exec) {
        this.exec = exec;
    }

    @ZenMethod
    public boolean isExec() {
        return exec;
    }

    @ZenMethod
    public void setExec(boolean exec) {
        this.exec = exec;
    }

    @ZenMethod
    public static IsExecute New(boolean exec) {
        return new IsExecute(exec);
    }
}
