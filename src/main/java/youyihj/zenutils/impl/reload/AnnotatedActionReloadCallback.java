package youyihj.zenutils.impl.reload;

import crafttweaker.IAction;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ActionReloadCallback;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public class AnnotatedActionReloadCallback extends ActionReloadCallback<IAction> {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, Optional<MethodHandle>> APPLY_RELOAD_METHOD_CACHE = new HashMap<>();
    private static final Map<Class<?>, Optional<MethodHandle>> UNDO_METHOD_CACHE = new HashMap<>();

    private final MethodHandle applyReloadMethod;
    private final MethodHandle undoMethod;

    public AnnotatedActionReloadCallback(IAction action) {
        super(action);
        this.applyReloadMethod = APPLY_RELOAD_METHOD_CACHE.computeIfAbsent(action.getClass(), it -> {
            try {
                return Optional.of(LOOKUP.findVirtual(action.getClass(), "applyReload", MethodType.methodType(Void.TYPE)));
            } catch (Exception e) {
                return Optional.empty();
            }
        }).orElse(null);
        this.undoMethod = UNDO_METHOD_CACHE.computeIfAbsent(action.getClass(), it -> {
            try {
                return Optional.of(LOOKUP.findVirtual(action.getClass(), "undo", MethodType.methodType(Void.TYPE)));
            } catch (Exception e) {
                return Optional.empty();
            }
        }).orElse(null);
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
