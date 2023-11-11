package youyihj.zenutils.impl.runtime;

public class ScriptRunException extends RuntimeException {
    public ScriptRunException(String message, Throwable cause) {
        super(message, cause, false, false);
    }

    public ScriptRunException(String message) {
        super(message);
    }
}
