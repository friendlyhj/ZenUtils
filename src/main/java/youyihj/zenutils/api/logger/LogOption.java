package youyihj.zenutils.api.logger;

import crafttweaker.CraftTweakerAPI;

import java.util.HashSet;
import java.util.Set;

/**
 * @author youyihj
 */
public class LogOption {
    private LogLevel minLogLevel = LogLevel.TRACE;
    private final Set<ILogFilter> filters = new HashSet<>();

    public LogLevel getMinLogLevel() {
        return minLogLevel;
    }

    public void setMinLogLevel(LogLevel minLogLevel) {
        this.minLogLevel = minLogLevel;
    }

    public void addFilter(ILogFilter filter) {
        filters.add(filter);
    }

    public boolean isBlock(LogLevel logLevel, String message) {
        fromCrTConfig();
        if (logLevel.compareTo(minLogLevel) < 0) {
            return true;
        }
        ScriptPosition scriptPosition = ScriptPosition.current();
        for (ILogFilter filter : filters) {
            if (filter.isBlock(message, logLevel, scriptPosition)) {
                return true;
            }
        }
        return false;
    }

    private void fromCrTConfig() {
        if (CraftTweakerAPI.isSuppressingErrors()) {
            minLogLevel = LogLevel.ERROR;
        } else if (CraftTweakerAPI.isSuppressingWarnings()) {
            minLogLevel = LogLevel.WARNING;
        }
    }
}
