package youyihj.zenutils.api.logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author youyihj
 */
public class RegexLogFilter implements ILogFilter {
    private final Pattern regex;

    public RegexLogFilter(Pattern regex) {
        this.regex = regex;
    }

    @Override
    public boolean isBlock(String message, LogLevel logLevel, @Nullable ScriptPosition scriptPosition) {
        return regex.matcher(message).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexLogFilter that = (RegexLogFilter) o;
        return Objects.equals(regex, that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex);
    }
}
