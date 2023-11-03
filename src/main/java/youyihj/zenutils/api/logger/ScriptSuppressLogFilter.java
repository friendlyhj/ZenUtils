package youyihj.zenutils.api.logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

/**
 * @author youyihj
 */
public class ScriptSuppressLogFilter implements ILogFilter {
    private final String scriptName;
    private final Set<LogLevel> suppressLevels;

    public ScriptSuppressLogFilter(String scriptName, Set<LogLevel> suppressLevels) {
        this.scriptName = scriptName;
        this.suppressLevels = suppressLevels;
    }

    @Override
    public boolean isBlock(String message, LogLevel logLevel, @Nullable ScriptPosition scriptPosition) {
        return scriptPosition != null && scriptName.equals(scriptPosition.getFileName()) && suppressLevels.contains(logLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptSuppressLogFilter that = (ScriptSuppressLogFilter) o;
        return Objects.equals(scriptName, that.scriptName) && Objects.equals(suppressLevels, that.suppressLevels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scriptName, suppressLevels);
    }
}
