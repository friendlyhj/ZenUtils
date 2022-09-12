package youyihj.zenutils.impl.delegate;

import crafttweaker.IAction;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.IScriptProvider;
import crafttweaker.runtime.ITweaker;
import crafttweaker.runtime.ScriptLoader;
import crafttweaker.runtime.events.CrTLoaderLoadingEvent;
import crafttweaker.runtime.events.CrTScriptLoadingEvent;
import crafttweaker.util.IEventHandler;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.Reloadable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;

public class ZenUtilsTweaker implements ITweaker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();

    private final ITweaker tweaker;
    private boolean freeze = false;
    private final Queue<IAction> reloadableActions = new LinkedList<>();
    private final Map<Class<?>, Boolean> actionReloadableCheck = new HashMap<>();
    private final Map<Class<?>, Optional<MethodHandle>> undoMethods = new HashMap<>();

    public ZenUtilsTweaker(ITweaker tweaker) {
        this.tweaker = tweaker;
    }

    @Override
    public void apply(IAction action) {
        boolean reloadable = isReloadable(action);
        if (!freeze || reloadable) {
            tweaker.apply(action);
        }
        if (reloadable && getUndoMethod(action).isPresent()) {
            reloadableActions.add(action);
        }
    }

    @Override
    public void setScriptProvider(IScriptProvider provider) {
        tweaker.setScriptProvider(provider);
    }

    @Override
    public void load() {
        tweaker.load();
    }

    @Override
    public boolean loadScript(boolean isSyntaxCommand, String loaderName) {
        return tweaker.loadScript(isSyntaxCommand, loaderName);
    }

    @Override
    public void loadScript(boolean isSyntaxCommand, ScriptLoader loader) {
        tweaker.loadScript(isSyntaxCommand, loader);
    }

    @Override
    public List<IAction> getActions() {
        return tweaker.getActions();
    }

    @Override
    public void enableDebug() {
        tweaker.enableDebug();
    }

    @Override
    public PreprocessorManager getPreprocessorManager() {
        return tweaker.getPreprocessorManager();
    }

    @Override
    public NetworkSide getNetworkSide() {
        return tweaker.getNetworkSide();
    }

    @Override
    public void setNetworkSide(NetworkSide networkSide) {
        tweaker.setNetworkSide(networkSide);
    }

    @Override
    public void registerLoadStartedEvent(IEventHandler<CrTLoaderLoadingEvent.Started> eventHandler) {
        tweaker.registerLoadStartedEvent(eventHandler);
    }

    @Override
    public void registerLoadFinishedEvent(IEventHandler<CrTLoaderLoadingEvent.Finished> eventHandler) {
        tweaker.registerLoadFinishedEvent(eventHandler);
    }

    @Override
    public void registerLoadAbortedEvent(IEventHandler<CrTLoaderLoadingEvent.Aborted> eventHandler) {
        tweaker.registerLoadAbortedEvent(eventHandler);
    }

    @Override
    public void registerScriptLoadPreEvent(IEventHandler<CrTScriptLoadingEvent.Pre> eventHandler) {
        tweaker.registerScriptLoadPreEvent(eventHandler);
    }

    @Override
    public void registerScriptLoadPostEvent(IEventHandler<CrTScriptLoadingEvent.Post> eventHandler) {
        tweaker.registerScriptLoadPostEvent(eventHandler);
    }

    @Override
    public List<ScriptLoader> getLoaders() {
        return tweaker.getLoaders();
    }

    @Override
    public ScriptLoader getOrCreateLoader(String... nameAndAliases) {
        return tweaker.getOrCreateLoader(nameAndAliases);
    }

    public void freezeActionApplying() {
        this.freeze = true;
    }

    public ITweaker getITweaker() {
        return tweaker;
    }

    public void onReload() {
        while (!reloadableActions.isEmpty()) {
            IAction action = reloadableActions.poll();
            getUndoMethod(action).ifPresent(methodHandle -> {
                try {
                    methodHandle.invoke(action);
                } catch (Throwable e) {
                    ZenUtils.forgeLogger.error("Failed to invoke undo method", e);
                }
            });
        }
    }

    private boolean isReloadable(IAction action) {
        return actionReloadableCheck.computeIfAbsent(action.getClass(), clazz -> clazz.isAnnotationPresent(Reloadable.class));
    }

    private Optional<MethodHandle> getUndoMethod(IAction action) {
        return undoMethods.computeIfAbsent(action.getClass(), clazz -> {
            try {
                return Optional.of(LOOKUP.findVirtual(clazz, "undo", MethodType.methodType(Void.TYPE)));
            } catch (Throwable e) {
                return Optional.empty();
            }
        });
    }
}
