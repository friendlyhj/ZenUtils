package youyihj.zenutils.impl.runtime;

/**
 * @author youyihj
 */
public enum ScriptStatus {
    INIT(false),
    RELOAD(true),
    STARTED(false),
    SYNTAX(true);

    private final boolean debug;

    ScriptStatus(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
