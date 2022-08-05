package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationContext")
public class CatenationContext {
    private IData data;
    private final Catenation catenation;

    public CatenationContext(Catenation catenation) {
        this.catenation = catenation;
    }

    @ZenGetter("data")
    @ZenMethod
    public IData getData() {
        if (data == null) {
            throw new IllegalStateException("Unable to call `getData` when data is null!");
        }
        return data;
    }

    @ZenSetter("data")
    @ZenMethod
    public void setData(IData data) {
        this.data = data;
    }

    @ZenMethod
    public void stop() {
        catenation.stop();
    }
}
