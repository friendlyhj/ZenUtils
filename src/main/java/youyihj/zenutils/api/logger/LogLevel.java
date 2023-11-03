package youyihj.zenutils.api.logger;

import net.minecraft.util.text.TextFormatting;

/**
 * @author youyihj
 */
public enum LogLevel {
    TRACE,
    INFO,
    WARNING(TextFormatting.YELLOW),
    ERROR(TextFormatting.RED),
    FATAL(TextFormatting.DARK_RED);

    private final TextFormatting textFormatting;
    private final boolean logPlayer;

    LogLevel(TextFormatting textFormatting) {
        this.textFormatting = textFormatting;
        this.logPlayer = true;
    }

    LogLevel() {
        this.logPlayer = false;
        this.textFormatting = TextFormatting.RESET;
    }

    public boolean isLogPlayer() {
        return logPlayer;
    }

    public TextFormatting getTextFormatting() {
        return textFormatting;
    }
}
