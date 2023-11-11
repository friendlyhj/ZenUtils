package youyihj.zenutils.impl.runtime;

import crafttweaker.api.player.IPlayer;
import crafttweaker.runtime.ILogger;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
public class HardFailLogger implements ILogger {
    @Override
    public void logCommand(String message) {

    }

    @Override
    public void logInfo(String message) {

    }

    @Override
    public void logWarning(String message) {

    }

    @Override
    public void logError(String message) {
        if (InternalUtils.getScriptStatus().isDebug()) {
            return;
        }
        throw new ScriptRunException(message);
    }

    @Override
    public void logError(String message, Throwable exception) {
        if (InternalUtils.getScriptStatus().isDebug()) {
            return;
        }
        if (exception instanceof ScriptRunException) {
            if (exception.getCause() == null) {
                throw ((ScriptRunException) exception);
            } else {
                throw new ScriptRunException(message, exception.getCause());
            }
        } else {
            throw new ScriptRunException(message, exception);
        }
    }

    @Override
    public void logPlayer(IPlayer player) {

    }

    @Override
    public void logDefault(String message) {

    }

    @Override
    public boolean isLogDisabled() {
        return false;
    }

    @Override
    public void setLogDisabled(boolean logDisabled) {

    }
}
