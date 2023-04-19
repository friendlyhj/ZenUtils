package youyihj.zenutils.api.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
public interface IActionReloadCallback<T extends IAction> {
    static <T extends IAction> void register(Class<T> clazz, IActionReloadCallback<T> callback) {
        ZenUtils.tweaker.addReloadCallback(clazz, callback);
    }

    default void applyReload(T action) {
        action.apply();
    }

    default void undo(T action) {

    }

    boolean hasUndoMethod();
}
