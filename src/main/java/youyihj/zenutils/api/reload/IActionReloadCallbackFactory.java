package youyihj.zenutils.api.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
public interface IActionReloadCallbackFactory<T extends IAction> {
    static <T extends IAction> void register(Class<T> clazz, IActionReloadCallbackFactory<T> callback) {
        ZenUtils.tweaker.addReloadCallback(clazz, callback);
    }

    ActionReloadCallback<T> create(T action);
}
