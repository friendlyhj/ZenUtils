package youyihj.zenutils.api.util.catenation;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.*;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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

    private final Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> objectHolders = new HashMap<>();

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

    @ZenMethod
    @ZenGetter
    public boolean hasData() {
        return this.data != null;
    }

    @ZenGetter("status")
    @ZenMethod
    public CatenationStatus getStatus() {
        return status;
    }

    // not exposed
    public void setStatus(CatenationStatus status, IWorld world) {
        if (!this.getStatus().isStop()) {
            this.status = status;
            if (this.getStatus().isStop() && this.onStop != null && world != null) {
                try {
                    this.onStop.apply(world, this);
                } catch (Exception e) {
                    CraftTweakerAPI.logError("Exception occurred in onStop function", e);
                }
            }
        }
    }

    public Map<ICatenationObjectHolder.Key<?>, ICatenationObjectHolder<?>> getObjectHolders() {
        return objectHolders;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, ICatenationObjectHolder.Type<T> type) {
        return (T) objectHolders.get(ICatenationObjectHolder.Key.of(key, type)).getValue();
    }

    @ZenMethod
    public void stop() {
        catenation.stop();
    }

    @ZenMethod
    public IPlayer getPlayer(@Optional("player") String key) {
        return getObject(key, BuiltinObjectHolderTypes.PLAYER);
    }
}
