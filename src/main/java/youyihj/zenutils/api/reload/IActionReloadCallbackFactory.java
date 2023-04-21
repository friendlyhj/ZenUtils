package youyihj.zenutils.api.reload;

import crafttweaker.IAction;
import crafttweaker.mc1120.events.ScriptRunEvent;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
@FunctionalInterface
public interface IActionReloadCallbackFactory<T extends IAction> {
    /**
     * Marks an action (and actions that extend it) is reloadable.
     * It should be used in {@link ScriptRunEvent.Pre} event listener
     * @param clazz the type of target action
     * @param callbackFactory describe how to create a reload callback object to the given action
     */
    static <T extends IAction> void register(Class<T> clazz, IActionReloadCallbackFactory<T> callbackFactory) {
        ZenUtils.tweaker.addReloadCallback(clazz, callbackFactory);
    }

    ActionReloadCallback<T> create(T action);
}
