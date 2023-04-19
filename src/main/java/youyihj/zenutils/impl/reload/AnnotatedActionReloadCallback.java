package youyihj.zenutils.impl.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ActionReloadCallback;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author youyihj
 */
public class AnnotatedActionReloadCallback extends ActionReloadCallback<IAction> {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private MethodHandle applyReloadMethod;
    private MethodHandle undoMethod;

    public AnnotatedActionReloadCallback(IAction action) {
        super(action);
        try {
            this.applyReloadMethod = LOOKUP.findVirtual(action.getClass(), "applyReload", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            this.applyReloadMethod = null;
        }
        try {
            this.undoMethod = LOOKUP.findVirtual(action.getClass(), "undo", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            this.undoMethod = null;
        }
    }

    @Override
    public void applyReload() {
        if (applyReloadMethod != null) {
            try {
                applyReloadMethod.invoke(action);
            } catch (Throwable e) {
                ZenUtils.forgeLogger.error("Failed to invoke applyReload method", e);
            }
        } else {
            action.apply();
        }
    }

    @Override
    public void undo() {
        if (undoMethod != null) {
            try {
                undoMethod.invoke(action);
            } catch (Throwable e) {
                ZenUtils.forgeLogger.error("Failed to invoke undo method", e);
            }
        }
    }

    @Override
    public boolean hasUndoMethod() {
        return undoMethod != null;
    }
}
