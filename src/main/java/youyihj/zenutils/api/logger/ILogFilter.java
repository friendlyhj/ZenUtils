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

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
