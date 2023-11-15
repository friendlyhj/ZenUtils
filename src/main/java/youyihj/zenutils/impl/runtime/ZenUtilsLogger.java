package youyihj.zenutils.impl.runtime;

import crafttweaker.api.logger.MTLogger;
import crafttweaker.api.player.IPlayer;
import crafttweaker.runtime.ILogger;
import youyihj.zenutils.api.logger.ICleanableLogger;
import youyihj.zenutils.api.logger.LogLevel;
import youyihj.zenutils.api.logger.LogOption;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author youyihj
 */
public class ZenUtilsLogger extends MTLogger implements ICleanableLogger {
    private boolean hasError = false;
    private final List<ILogger> loggers = new ArrayList<>();

    private final List<String> messagesToSendPlayer = new ArrayList<>();
    private final List<IPlayer> playerList = new ArrayList<>();
    private LogOption logOption = new LogOption();

    @Override
    public void addLogger(ILogger logger) {
        loggers.add(logger);
    }

    @Override
    public void removeLogger(ILogger logger) {
        loggers.remove(logger);
    }

    @Override
    public void addPlayer(IPlayer player) {
        playerList.add(player);
        logPlayer(player);
    }

    @Override
    public void removePlayer(IPlayer player) {
        playerList.remove(player);
    }

    @Override
    public void clear() {
        messagesToSendPlayer.clear();
        logOption = new LogOption();
        hasError = false;
    }

    @Override
    public void logCommand(String message) {
        loggers.forEach(logger -> logger.logCommand(message));
    }

    @Override
    public void logInfo(String message) {
        log(LogLevel.INFO, message, null, logger -> logger.logInfo(message));
    }

    @Override
    public void logWarning(String message) {
        log(LogLevel.WARNING, message, null, logger -> logger.logWarning(message));
    }

    @Override
    public void logError(String message) {
        log(LogLevel.ERROR, message, null, logger -> logger.logError(message));
    }

    @Override
    public void logError(String message, Throwable exception) {
        log(LogLevel.FATAL, message, exception, logger -> logger.logError(message, exception));
    }

    @Override
    public void logPlayer(IPlayer player) {
        messagesToSendPlayer.forEach(player::sendChat);
    }

    @Override
    public void logDefault(String message) {
        log(LogLevel.TRACE, message, null, logger -> logger.logDefault(message));
    }

    @Override
    public boolean isLogDisabled() {
        return logOption.getMinLogLevel().compareTo(LogLevel.TRACE) > 0;
    }

    @Override
    public void setLogDisabled(boolean logDisabled) {
        if (!isLogDisabled()) {
            logOption.setMinLogLevel(LogLevel.INFO);
        }
    }

    @Override
    public void clean() {
        messagesToSendPlayer.clear();
        loggers.stream()
                .filter(ICleanableLogger.class::isInstance)
                .map(ICleanableLogger.class::cast)
                .forEach(ICleanableLogger::clean);
    }

    private void log(LogLevel logLevel, String message, Throwable exception, Consumer<ILogger> loggerConsumer) {
        if (logOption.isBlock(logLevel, message))
            return;
        loggers.forEach(loggerConsumer);
        if (logLevel.isLogPlayer()) {
            if (playerList.isEmpty()) {
                messagesToSendPlayer.add(logLevel.getTextFormatting() + getMessageToSendPlayer(message, exception));
            } else {
                playerList.forEach(it -> it.sendChat(logLevel.getTextFormatting() + getMessageToSendPlayer(message, exception)));
            }
        }
    }

    @Override
    public String getMessageToSendPlayer(String message, Throwable exception) {
        if (exception == null) return message;
        if (message == null) return String.valueOf(exception);
        if (exception.getMessage() != null && message.endsWith(exception.getMessage())) {
            return message.substring(0, message.length() - exception.getMessage().length()) + exception;
        }
        return message + ", caused by " + exception;
    }

    public LogOption getLogOption() {
        return logOption;
    }

    public boolean hasError() {
        return hasError;
    }
}
