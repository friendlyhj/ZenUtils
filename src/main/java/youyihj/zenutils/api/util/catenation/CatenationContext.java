package youyihj.zenutils.api.util.catenation;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationContext")
public class CatenationContext {
    private IData data;
    private final Catenation catenation;
    @Nullable
    private final IWorldFunction onStop;
    private CatenationStatus status = CatenationStatus.WORKING;

    public CatenationContext(Catenation catenation, @Nullable IWorldFunction onStop) {
        this.catenation = catenation;
        this.onStop = onStop;
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

    @ZenGetter("status")
    @ZenMethod
    public CatenationStatus getStatus() {
        return status;
    }

    // not exposed
    public void setStatus(CatenationStatus status, IWorld world) {
        if (!this.status.isStop() && status.isStop()) {
            this.status = status;
            if (this.onStop != null && world != null) {
                try {
                    this.onStop.apply(world, this);
                } catch (Exception e) {
                    CraftTweakerAPI.logError("Exception occurred in onStop function", e);
                }
            }
        }
    }

    @ZenMethod
    public void stop() {
        catenation.stop();
    }
}
