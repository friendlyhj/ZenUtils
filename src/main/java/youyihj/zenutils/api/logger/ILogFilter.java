package youyihj.zenutils.api.logger;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public interface ILogFilter {
    /**
     * returns true to block the log message
     */
    boolean isBlock(String message, LogLevel logLevel, @Nullable ScriptPosition scriptPosition);

    int hashCode();

    boolean equals(Object obj);
}
