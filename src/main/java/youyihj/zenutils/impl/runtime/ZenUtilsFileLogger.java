package youyihj.zenutils.impl.runtime;

import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.common.thread.SidedThreadGroup;
import net.minecraftforge.fml.relauncher.Side;
import youyihj.zenutils.api.logger.ICleanableLogger;
import youyihj.zenutils.api.logger.LogLevel;
import youyihj.zenutils.impl.util.InternalUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * @author youyihj
 */
public class ZenUtilsFileLogger implements ICleanableLogger {
    private final Path output;
    private PrintWriter printWriter;
    private boolean disableTrace;

    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");


    public ZenUtilsFileLogger(Path output) {
        this.output = output;
        openWriter();
    }

    @Override
    public void logCommand(String message) {
        printWriter.println(getTextWithoutFormattingCodes(message));
    }

    @Override
    public void logInfo(String message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void logWarning(String message) {
        log(LogLevel.WARNING, message);
    }

    @Override
    public void logError(String message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void logError(String message, Throwable exception) {
        log(LogLevel.FATAL, message);
        if (exception != null) {
            exception.printStackTrace(printWriter);
        }
    }

    @Override
    public void logPlayer(IPlayer player) {
        // NO-OP
    }

    @Override
    public void logDefault(String message) {
        if (!disableTrace) {
            log(LogLevel.TRACE, message);
        }
    }

    @Override
    public boolean isLogDisabled() {
        return disableTrace;
    }

    @Override
    public void setLogDisabled(boolean logDisabled) {
        this.disableTrace = logDisabled;
    }

    @Override
    public void clean() {
        printWriter.close();
        openWriter();
        log(LogLevel.TRACE, "Requested to clean log at " + LocalTime.now());
    }

    private void log(LogLevel level, String message) {
        printWriter.printf("[%s][%s][%s] %s", InternalUtils.getLoaderState(), getEffectiveSide(), level, getTextWithoutFormattingCodes(message));
        printWriter.println();
    }

    private void openWriter() {
        try {
            Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
            this.printWriter = new PrintWriter(writer, true);
        } catch (IOException e) {
            throw new RuntimeException("Could not open log file " + output);
        }
    }

    // copied from TextFormatting, avoid early class loading
    private String getTextWithoutFormattingCodes(String text) {
        return text == null ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
    }

    // copied from FMLCommonHandler, avoid early class loading
    private Side getEffectiveSide() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        return group instanceof SidedThreadGroup ? ((SidedThreadGroup) group).getSide() : Side.CLIENT;
    }
}
