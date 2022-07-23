package youyihj.zenutils.impl.delegate;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.IScriptProvider;
import crafttweaker.runtime.ITweaker;
import crafttweaker.runtime.ScriptLoader;
import crafttweaker.runtime.events.CrTLoaderLoadingEvent;
import crafttweaker.runtime.events.CrTScriptLoadingEvent;
import crafttweaker.util.IEventHandler;

import java.util.List;

public class ZenUtilsTweaker implements ITweaker {
    private final ITweaker tweaker;
    private boolean freeze = false;

    public ZenUtilsTweaker(ITweaker tweaker) {
        this.tweaker = tweaker;
    }

    @Override
    public void apply(IAction action) {
        if (!freeze) {
            tweaker.apply(action);
        } else {
            CraftTweakerAPI.logWarning("Action application is freezing! Recipe modification is not reloadable.");
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
}
