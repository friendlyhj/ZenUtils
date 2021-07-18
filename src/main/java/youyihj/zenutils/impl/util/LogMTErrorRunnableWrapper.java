package youyihj.zenutils.impl.util;

import crafttweaker.CraftTweakerAPI;

/**
 * @author youyihj
 */
public class LogMTErrorRunnableWrapper implements Runnable {
    private final Runnable internal;

    private LogMTErrorRunnableWrapper(Runnable internal) {
        this.internal = internal;
    }

    public static LogMTErrorRunnableWrapper create(Runnable internal) {
        return new LogMTErrorRunnableWrapper(internal);
    }

    @Override
    public void run() {
        try {
            internal.run();
        } catch (Throwable throwable) {
            CraftTweakerAPI.logError(null, throwable);
        }
    }
}
