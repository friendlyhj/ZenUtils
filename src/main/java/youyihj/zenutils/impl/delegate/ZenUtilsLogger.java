package youyihj.zenutils.impl.delegate;

import crafttweaker.api.logger.MTLogger;
import crafttweaker.api.player.IPlayer;
import crafttweaker.runtime.ILogger;
import crafttweaker.util.SuppressErrorFlag;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;
import youyihj.zenutils.impl.util.ScriptRunException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author youyihj
 */
public class ZenUtilsLogger extends MTLogger implements ILogger {
    private final MTLogger mtLogger;
    private static Field mtLoggersField;
    private boolean hasError = false;

    private final List<String> messagesToSendPlayer = new ArrayList<>();
    private final List<IPlayer> playerList = new ArrayList<>();
    private final Set<Pattern> filterLogRegexes = new TreeSet<>(Comparator.comparing(Pattern::toString));

    static {
        try {
            Class<?> clazz = MTLogger.class;
            mtLoggersField = ReflectUtils.removePrivateFinal(clazz, "loggers");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ZenUtilsLogger(MTLogger mtLogger) {
        this.mtLogger = mtLogger;
    }

    @Override
    public void addLogger(ILogger logger) {
        mtLogger.addLogger(logger);
    }

    @Override
    public void removeLogger(ILogger logger) {
        mtLogger.removeLogger(logger);
    }

    @Override
    public void addPlayer(IPlayer player) {
        if (InternalUtils.onSuppressErrorSingleScriptMode()) {
            playerList.add(player);
            this.logPlayer(player);
        } else {
            mtLogger.addPlayer(player);
        }
    }

    @Override
    public void removePlayer(IPlayer player) {
        if (InternalUtils.onSuppressErrorSingleScriptMode()) {
            playerList.remove(player);
        } else {
            mtLogger.removePlayer(player);
        }
    }

    @Override
    public void clear() {
        mtLogger.clear();
        messagesToSendPlayer.clear();
    }

    @Override
    public void logCommand(String message) {
        if (shouldNotLog(message)) return;
        mtLogger.logCommand(message);
    }

    @Override
    public void logInfo(String message) {
        if (shouldNotLog(message)) return;
        mtLogger.logInfo(message);
    }

    @Override
    public void logWarning(String message) {
        if (shouldNotLog(message)) return;
        if (InternalUtils.onSuppressErrorSingleScriptMode()) {
            getLoggers().forEach(logger -> logger.logWarning(message));

            SuppressErrorFlag errorFlag = InternalUtils.getCurrentSuppressErrorFlag();
            if (errorFlag.isSuppressingWarnings())
                return;

            String message2 = "\u00a7eWARNING: " + message;
            if (playerList.isEmpty()) {
                messagesToSendPlayer.add(message2);
            } else {
                playerList.forEach(player -> player.sendChat(message2));
            }
        } else {
            mtLogger.logWarning(message);
        }
    }

    @Override
    public void logError(String message) {
        this.logError(message, null);
    }

    @Override
    public void logError(String message, Throwable exception) {
        if (shouldNotLog(message)) return;
        if (InternalUtils.onSuppressErrorSingleScriptMode()) {
            getLoggers().forEach(logger -> logger.logError(message, exception));

            SuppressErrorFlag errorFlag = InternalUtils.getCurrentSuppressErrorFlag();
            if (errorFlag.isSuppressingErrors())
                return;

            if (InternalUtils.hardFailMode) {
                if (exception == null) {
                    throw new ScriptRunException(message);
                } else {
                    throw new ScriptRunException(message, exception);
                }
            }
            String message2 = "\u00a7cERROR: " + super.getMessageToSendPlayer(message, exception);
            this.hasError = true;
            if (playerList.isEmpty()) {
                messagesToSendPlayer.add(message2);
            } else {
                playerList.forEach(player -> player.sendChat(message2));
            }
        } else {
            mtLogger.logError(message, exception);
        }
    }

    @Override
    public void logPlayer(IPlayer player) {
        messagesToSendPlayer.forEach(player::sendChat);
    }

    @Override
    public void logDefault(String message) {
        if (shouldNotLog(message)) return;
        mtLogger.logDefault(message);
    }

    @Override
    public boolean isLogDisabled() {
        return mtLogger.isLogDisabled();
    }

    @Override
    public void setLogDisabled(boolean logDisabled) {
        mtLogger.setLogDisabled(logDisabled);
    }

    @SuppressWarnings("unchecked")
    private List<ILogger> getLoggers() {
        try {
            return (List<ILogger>) mtLoggersField.get(mtLogger);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addRegexLogFilter(String regex) {
        filterLogRegexes.add(Pattern.compile(regex));
    }

    private boolean shouldNotLog(String message) {
        if (message == null) return false;
        return filterLogRegexes.stream().anyMatch(pattern -> pattern.matcher(message).matches());
    }

    public boolean hasError() {
        return hasError;
    }
}
