package youyihj.zenutils.api.reload;

import crafttweaker.IAction;

/**
 * @author youyihj
 */
public abstract class ActionReloadCallback<T extends IAction> {
    protected final T action;

    public ActionReloadCallback(T action) {
        this.action = action;
    }

    public void applyReload() {
        action.apply();
    }

    public void undo() {

    }

    public void beforeApply(@SuppressWarnings("unused") boolean reload) {

    }

    public void afterApply(@SuppressWarnings("unused") boolean reload) {

    }

    public abstract boolean hasUndoMethod();
}
