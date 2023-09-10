package youyihj.zenutils.api.reload;

import crafttweaker.IAction;


/**
 * Handles an existed action reload callback behaviors. Each action object has a reload callback object. Extend it to describe
 * your reload behavior for an action.
 *
 * @see IActionReloadCallbackFactory
 */
public abstract class ActionReloadCallback<T extends IAction> {
    protected final T action;

    public ActionReloadCallback(T action) {
        this.action = action;
    }

    /**
     * Same as {@link IAction#apply()} but will be called on reloading
     */
    public void applyReload() {
        action.apply();
    }

    /**
     * Rolls back changes carried by the action.
     */
    public void undo() {

    }

    public void beforeApply(@SuppressWarnings("unused") boolean reload) {

    }

    public void afterApply(@SuppressWarnings("unused") boolean reload) {

    }

    /**
     * @return If the action requires some cleanup code
     */
    public abstract boolean hasUndoMethod();

    public String describeAction() {
        return action.describe();
    }
}
