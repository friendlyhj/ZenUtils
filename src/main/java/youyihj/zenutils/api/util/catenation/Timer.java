package youyihj.zenutils.api.util.catenation;

import com.google.common.base.Preconditions;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationTimer")
public class Timer {
    private final long duration;
    private long current;

    public Timer(long duration) {
        Preconditions.checkArgument(duration > 0, "duration must be positive.");
        this.duration = duration;
    }

    @ZenMethod
    public void update() {
        if (!isFinish()) {
            current++;
        }
    }

    @ZenMethod
    public void reset() {
        setCurrent(0);
    }

    @ZenGetter("current")
    public long getCurrent() {
        return current;
    }

    @ZenSetter("current")
    public void setCurrent(long current) {
        this.current = Math.min(duration, current);
    }

    @ZenGetter("duration")
    public long getDuration() {
        return duration;
    }

    @ZenGetter("finish")
    public boolean isFinish() {
        return current == duration;
    }
}
