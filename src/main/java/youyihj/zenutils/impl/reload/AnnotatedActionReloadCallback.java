package youyihj.zenutils.impl.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.IActionReloadCallback;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author youyihj
 */
public class AnnotatedActionReloadCallback implements IActionReloadCallback<IAction> {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private MethodHandle applyReloadMethod;
    private MethodHandle undoMethod;

    public <T extends IAction> AnnotatedActionReloadCallback(Class<T> clazz) {
        try {
            this.applyReloadMethod = LOOKUP.findVirtual(clazz, "applyReload", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            this.applyReloadMethod = null;
        }
        try {
            this.undoMethod = LOOKUP.findVirtual(clazz, "undo", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            this.undoMethod = null;
        }
    }

    @Override
    public void applyReload(IAction action) {
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
    public void undo(IAction action) {
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
