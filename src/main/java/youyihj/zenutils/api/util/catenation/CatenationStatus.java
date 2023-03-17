package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.*;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationStatus")
public enum CatenationStatus {
    WORKING,
    PAUSE,
    FINISH(true, false),
    STOP_MANUAL(true, false),
    STOP_INTERNAL(true, false),
    ERROR(true, false),
    UNLOAD(true, true),
    SERIAL;

    private final boolean stop;
    private final boolean rerun;

    CatenationStatus() {
        this(false, true);
    }

    CatenationStatus(boolean stop, boolean rerun) {
        this.stop = stop;
        this.rerun = rerun;
    }

    @ZenMethod
    public static CatenationStatus working() {
        return WORKING;
    }

    @ZenMethod
    public static CatenationStatus pause() {
        return PAUSE;
    }

    @ZenMethod
    public static CatenationStatus finish() {
        return FINISH;
    }

    @ZenMethod
    public static CatenationStatus stopManual() {
        return STOP_MANUAL;
    }

    @ZenMethod
    public static CatenationStatus stopInternal() {
        return STOP_INTERNAL;
    }

    @ZenMethod
    public static CatenationStatus error() {
        return ERROR;
    }

    @ZenMethod
    public static CatenationStatus unload() {
        return UNLOAD;
    }

    @ZenMethod
    public static CatenationStatus serial() {
        return SERIAL;
    }

    @ZenMethod
    @ZenGetter("isStop")
    public boolean isStop() {
        return stop;
    }

    @ZenMethod
    @ZenGetter("isRerun")
    public boolean isRerun() {
        return rerun;
    }

    @ZenOperator(OperatorType.EQUALS)
    public boolean zenEquals(CatenationStatus status) {
        return equals(status);
    }

    @ZenGetter("name")
    public String zenName() {
        return name();
    }
}
