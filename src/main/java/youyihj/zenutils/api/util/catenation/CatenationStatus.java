package youyihj.zenutils.api.util.catenation;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.*;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationStatus")
public enum CatenationStatus {
    WORKING(false),
    PAUSE(false),
    FINISH,
    STOP_MANUAL,
    STOP_INTERNAL,
    ERROR,
    UNLOAD(false),
    SERIAL(false);

    private final boolean stop;

    CatenationStatus() {
        this(true);
    }

    CatenationStatus(boolean stop) {
        this.stop = stop;
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
    @ZenGetter("isStop")
    public boolean isStop() {
        return stop;
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
