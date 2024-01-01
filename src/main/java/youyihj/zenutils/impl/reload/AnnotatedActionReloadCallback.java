package youyihj.zenutils.impl.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ActionReloadCallback;
import youyihj.zenutils.impl.util.SimpleCache;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author youyihj
 */
public class AnnotatedActionReloadCallback extends ActionReloadCallback<IAction> {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final SimpleCache<Class<?>, MethodHandle> APPLY_RELOAD_METHOD_CACHE = new SimpleCache<>(AnnotatedActionReloadCallback::findApplyReloadMethod);
    private static final SimpleCache<Class<?>, MethodHandle> UNDO_METHOD_CACHE = new SimpleCache<>(AnnotatedActionReloadCallback::findUndoMethod);

    private final MethodHandle applyReloadMethod;
    private final MethodHandle undoMethod;

    public AnnotatedActionReloadCallback(IAction action) {
        super(action);
        this.applyReloadMethod = APPLY_RELOAD_METHOD_CACHE.get(action.getClass());
        this.undoMethod = UNDO_METHOD_CACHE.get(action.getClass());
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

    private static MethodHandle findUndoMethod(Class<?> clazz) {
        try {
            return LOOKUP.findVirtual(clazz, "undo", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            return null;
        }
    }

    private static MethodHandle findApplyReloadMethod(Class<?> clazz) {
        try {
            return LOOKUP.findVirtual(clazz, "applyReload", MethodType.methodType(Void.TYPE));
        } catch (Exception e) {
            return null;
        }
    }
}
