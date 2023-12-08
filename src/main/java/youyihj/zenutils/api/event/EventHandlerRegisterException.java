package youyihj.zenutils.api.event;

/**
 * @author youyihj
 */
public class EventHandlerRegisterException extends Exception {
    public EventHandlerRegisterException(String message) {
        super(message);
    }

    public EventHandlerRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
