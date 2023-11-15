package youyihj.zenutils.impl.runtime;

import crafttweaker.api.player.IPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import youyihj.zenutils.api.logger.ICleanableLogger;
import youyihj.zenutils.api.logger.LogLevel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;

/**
 * @author youyihj
 */
public class ZenUtilsFileLogger implements ICleanableLogger {
    private final Path output;
    private PrintWriter printWriter;
    private boolean disableTrace;

    public ZenUtilsFileLogger(Path output) {
        this.output = output;
        openWriter();
    }

    @Override
    public void logCommand(String message) {
        printWriter.println(TextFormatting.getTextWithoutFormattingCodes(message));
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
        printWriter.printf("[%s][%s][%s] %s", Loader.instance().getLoaderState(), FMLCommonHandler.instance().getEffectiveSide(), level, TextFormatting.getTextWithoutFormattingCodes(message));
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
}
