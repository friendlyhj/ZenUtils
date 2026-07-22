package youyihj.zenutils.api.logger;

/**
 * @author youyihj
 */
public enum LogLevel {
    TRACE,
    INFO,
    WARNING('e'),
    ERROR('c'),
    FATAL('4');

    private final char controlText;
    private final boolean logPlayer;

    LogLevel(char controlText) {
        this.controlText = controlText;
        this.logPlayer = true;
    }

    LogLevel() {
        this.controlText = 'r';
        this.logPlayer = false;
    }

    public boolean isLogPlayer() {
        return logPlayer;
    }

    public String getControlText() {
        return "§" + controlText;
    }
}
