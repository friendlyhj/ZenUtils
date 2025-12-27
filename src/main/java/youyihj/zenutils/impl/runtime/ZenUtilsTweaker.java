package youyihj.zenutils.impl.runtime;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.CrTScriptLoadEvent;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.*;
import crafttweaker.runtime.events.CrTLoaderLoadingEvent;
import crafttweaker.runtime.events.CrTScriptLoadingEvent;
import crafttweaker.runtime.providers.ScriptProviderDirectory;
import crafttweaker.util.IEventHandler;
import crafttweaker.util.SuppressErrorFlag;
import crafttweaker.zenscript.CrtStoringErrorLogger;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import rml.layer.compat.crt.RMLCrTLoader;
import stanhebben.zenscript.ZenModule;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.type.ZenType;
import youyihj.zenutils.api.reload.ActionReloadCallback;
import youyihj.zenutils.api.reload.IActionReloadCallbackFactory;
import youyihj.zenutils.api.reload.Reloadable;
import youyihj.zenutils.impl.core.Configuration;
import youyihj.zenutils.impl.reload.AnnotatedActionReloadCallback;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static stanhebben.zenscript.ZenModule.compileScripts;
import static stanhebben.zenscript.ZenModule.extractClassName;

public class ZenUtilsTweaker implements ITweaker {
    private final ITweaker tweaker;
    private IScriptProvider scriptProvider;
    private boolean freeze = false;
    private final Queue<ActionReloadCallback<?>> reloadableActions = new ArrayDeque<>();
    private final Map<Class<?>, IActionReloadCallbackFactory<?>> reloadCallbacks = new HashMap<>();
    private String currentLoader = null;

    private boolean scriptDispatched = false;
    private final Multimap<String, ScriptFile> loaderTasks = ArrayListMultimap.create();

    private ZenUtilsGlobalEnvironment lastEnvironment = null;

    public ZenUtilsTweaker(ITweaker tweaker) {
        this.tweaker = tweaker;
        File globalDir = new File("scripts");
        if (!globalDir.exists())
            globalDir.mkdirs();
        ScriptProviderDirectory provider = new ScriptProviderDirectory(globalDir);
        if (!Loader.isModLoaded("rml")) {
            setScriptProvider(provider);
        } else {
            // zenutils tweaker breaks rml crt support, we fix it here
            MinecraftForge.EVENT_BUS.register(RMLCrTLoader.class);
            setScriptProvider(RMLCrTLoader.inject(provider));
        }
    }

    @Override
    public void apply(IAction action) {
        ActionReloadCallback<IAction> reloadCallback = getReloadCallback(action);
        boolean reloadable = reloadCallback != null;
        if (!freeze) {
            if (validateAction(action)) return;
            try {
                if (reloadable) reloadCallback.beforeApply(false);
                action.apply();
                if (reloadable) reloadCallback.afterApply(false);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Failed to apply this action", e);
            }
        } else if (reloadable) {
            if (validateAction(action)) return;
            try {
                reloadCallback.beforeApply(true);
                reloadCallback.applyReload();
                reloadCallback.afterApply(true);
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Failed to apply this action", e);
            }
        } else {
            String describe = action.describe();
            if (describe != null && !describe.isEmpty()) {
                CraftTweakerAPI.logInfo("Tried to reload action: " + describe + ", which is not reloadable.");
            } else {
                CraftTweakerAPI.logInfo("Tried to reload an un-reloadable action: " + action);
            }
        }
        if (reloadable && reloadCallback.hasUndoMethod()) {
            reloadableActions.add(reloadCallback);
        }
    }

    @Override
    public void setScriptProvider(IScriptProvider provider) {
        tweaker.setScriptProvider(provider);
        this.scriptProvider = provider;
    }

    @Override
    public void load() {
        tweaker.load();
    }

    @Override
    public boolean loadScript(boolean isSyntaxCommand, String loaderName) {
        currentLoader = loaderName;
        try {
            resetPrimitiveCastingRules();
            rebuildLiteralTypesCache();
        } catch (Exception e) {
            CraftTweakerAPI.logError("Failed to reset type info", e);
        }
        ScriptStatus origin = InternalUtils.getScriptStatus();
        if (isSyntaxCommand) {
            InternalUtils.setScriptStatus(ScriptStatus.SYNTAX);
        }
        boolean result = loadScriptInternal(isSyntaxCommand, loaderName);
        if (isSyntaxCommand) {
            InternalUtils.setScriptStatus(origin);
        }
        currentLoader = null;
        return result;
    }

    @Override
    public void loadScript(boolean isSyntaxCommand, ScriptLoader loader) {
        currentLoader = loader.getMainName();
        ScriptStatus origin = InternalUtils.getScriptStatus();
        if (isSyntaxCommand) {
            InternalUtils.setScriptStatus(ScriptStatus.SYNTAX);
        }
        loadScriptInternal(isSyntaxCommand, loader);
        if (isSyntaxCommand) {
            InternalUtils.setScriptStatus(origin);
        }
        currentLoader = null;
    }

    private boolean loadScriptInternal(boolean isSyntaxCommand, ScriptLoader loader) {
        if (isSyntaxCommand) {
            CraftTweakerAPI.setSuppressErrorFlag(SuppressErrorFlag.FORCED);
            clearLoaderTasks();
        }

        if (loader == null) {
            CraftTweakerAPI.logError("Error when trying to load with a null loader");
            return false;
        }

        if (!Configuration.fastScriptLoading) {
            tweaker.loadScript(isSyntaxCommand, loader);
            return loader.getLoaderStage() == ScriptLoader.LoaderStage.LOADED_SUCCESSFUL;
        }

        if (!scriptDispatched) {
            CraftTweakerAPI.logInfo("Fast script loading enabled.");
            long startTime = System.currentTimeMillis();
            collectScriptFiles(isSyntaxCommand);
            CraftTweakerAPI.logDefault("Collected script files and preprocessors. took " + (System.currentTimeMillis() - startTime) + "ms");
            scriptDispatched = true;
        }

        CraftTweakerAPI.logInfo("Loading scripts for loader with names " + loader);
        if (loader.isLoaded() && !isSyntaxCommand) {
            CraftTweakerAPI.logDefault("Skipping loading for loader " + loader + " since it's already been loaded");
            return false;
        }

        if (loader.isDelayed() && !isSyntaxCommand) {
            CraftTweakerAPI.logDefault("Skipping loading for loader " + loader + " since its execution is being delayed by another mod.");
            return false;
        }

        if (loader.getLoaderStage() == ScriptLoader.LoaderStage.INVALIDATED) {
            CraftTweakerAPI.logWarning("Skipping loading for loader " + loader + " since it's become invalidated");
            return false;
        }

        loader.setLoaderStage(ScriptLoader.LoaderStage.LOADING);
        boolean loadSuccessful = true;
        ((CrtStoringErrorLogger) GlobalRegistry.getErrors()).clear();

        Map<String, byte[]> classes;
        IEnvironmentGlobal environmentGlobal;

        if (Configuration.crossLoaderAccess) {
            ZenUtilsGlobalEnvironment environment = new ZenUtilsGlobalEnvironment(lastEnvironment, loader.getMainName());
            environmentGlobal = environment;
            if (InternalUtils.getScriptStatus() == ScriptStatus.INIT) {
                lastEnvironment = environment;
            }
            classes = environment.getClasses();
        } else {
            classes = new HashMap<>();
            environmentGlobal = GlobalRegistry.makeGlobalEnvironment(classes, loader.getMainName());
        }

        long startTime = System.currentTimeMillis();
        List<ScriptFile> scriptFiles = loader.getNames().stream()
                .flatMap(name -> loaderTasks.get(name).stream())
                .distinct()
                .sorted(PreprocessorManager.SCRIPT_FILE_COMPARATOR)
                .collect(Collectors.toList());

        final String loaderName = loader.getMainName();

        String reloadInfix = InternalUtils.getScriptStatus().isDebug() ? base62(startTime) : "";
        if (!reloadInfix.isEmpty()) {
            CraftTweakerAPI.logDefault("Reload infix for this load: '" + reloadInfix + "'");
        }

        for (ScriptFile scriptFile : scriptFiles) {
            // update class name generator
            environmentGlobal.getClassNameGenerator().setPrefix(scriptFile.loaderNamesConcatCapitalized() + reloadInfix);

            // check for network side
            if (!scriptFile.shouldBeLoadedOn(getNetworkSide())) {
                CraftTweakerAPI.logDefault(getTweakerDescriptor(loaderName) + ": Skipping file " + scriptFile + " as we are on the wrong side of the Network");
                continue;
            }

            CraftTweakerAPI.logDefault(getTweakerDescriptor(loaderName) + ": Loading Script: " + scriptFile);

            ZenParsedFile zenParsedFile = null;
            String filename = scriptFile.getEffectiveName();
            String className = extractClassName(filename);

            // start reading of the scripts
            ZenTokener parser = null;
            try (Reader reader = new InputStreamReader(new BufferedInputStream(scriptFile.open()), StandardCharsets.UTF_8)) {
                getPreprocessorManager().postLoadEvent(new CrTScriptLoadEvent(scriptFile));

                // blocks the parsing of the script
                if (scriptFile.isParsingBlocked()) {
                    continue;
                }

                parser = new ZenTokener(reader, environmentGlobal.getEnvironment(), filename, scriptFile.areBracketErrorsIgnored());
                zenParsedFile = new ZenParsedFile(filename, className + reloadInfix, parser, environmentGlobal);

            } catch (IOException ex) {
                CraftTweakerAPI.logError(getTweakerDescriptor(loaderName) + ": Could not load script " + scriptFile + ": " + ex.getMessage());
                loadSuccessful = false;
            } catch (ParseException ex) {
                CraftTweakerAPI.logError(getTweakerDescriptor(loaderName) + ": Error parsing " + ex.getFile().getFileName() + ":" + ex.getLine() + " -- " + ex.getExplanation());
                loadSuccessful = false;
            } catch (Exception ex) {
                CraftTweakerAPI.logError(getTweakerDescriptor(loaderName) + ": Error loading " + scriptFile + ": " + ex, ex);
                loadSuccessful = false;
            }

            try {
                // Stops if compile is disabled
                if (zenParsedFile == null || scriptFile.isCompileBlocked() || !loadSuccessful) {
                    continue;
                }

                compileScripts(className, Collections.singletonList(zenParsedFile), environmentGlobal, scriptFile.isDebugEnabled());

                // stops if the execution is disabled
                if (scriptFile.isExecutionBlocked() || isSyntaxCommand) {
                    continue;
                }

                ZenModule module = new ZenModule(classes, CraftTweakerAPI.class.getClassLoader());
                Runnable runnable = module.getMain();
                if (runnable != null)
                    runnable.run();
            } catch (Throwable ex) {
                CraftTweakerAPI.logError(getTweakerDescriptor(loaderName) + ": Error executing " + scriptFile + ": " + ex.getMessage(), ex);
            }
        }

        loader.setLoaderStage(loadSuccessful ? ScriptLoader.LoaderStage.LOADED_SUCCESSFUL : ScriptLoader.LoaderStage.ERROR);
        CraftTweakerAPI.logDefault("Completed script loading in: " + (System.currentTimeMillis() - startTime) + "ms");

        if (isSyntaxCommand) {
            clearLoaderTasks();
        }

        return loadSuccessful;
    }

    private boolean loadScriptInternal(boolean isSyntaxCommand, String loaderName) {
        if (!Configuration.fastScriptLoading) {
            return tweaker.loadScript(isSyntaxCommand, loaderName);
        }
        return loadScriptInternal(isSyntaxCommand, getOrCreateLoader(loaderName).removeDelay(loaderName));
    }

    private void collectScriptFiles(boolean isSyntaxCommand) {
        List<ScriptFile> scriptFiles = new ArrayList<>();
        HashSet<String> collected = new HashSet<>();

        // Collecting all scripts
        Iterator<IScriptIterator> scripts = scriptProvider.getScripts();
        while (scripts.hasNext()) {
            IScriptIterator script = scripts.next();

            if (!collected.contains(script.getGroupName())) {
                collected.add(script.getGroupName());

                while (script.next()) {
                    scriptFiles.add(new ScriptFile(this, script.copyCurrent(), isSyntaxCommand));
                }
            }
        }

        // Collecting all preprocessors
        for (ScriptFile scriptFile : scriptFiles) {
            scriptFile.addAll(getPreprocessorManager().checkFileForPreprocessors(scriptFile));
        }

        // Dispatching all loader tasks
        for (ScriptFile scriptFile : scriptFiles) {
            String[] loaderNames = scriptFile.getLoaderNames();
            for (String loaderName : loaderNames) {
                loaderTasks.put(loaderName, scriptFile);
            }
        }
    }

    private String getTweakerDescriptor(String loaderName) {
        return "[" + loaderName + " | " + getNetworkSide() + "]";
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
        if (Configuration.fastScriptLoading) {
            CraftTweakerAPI.logWarning("Fast script loading disabled registerLoadStartedEvent. Please tell the mod author!");
        }
        tweaker.registerLoadStartedEvent(eventHandler);
    }

    @Override
    public void registerLoadFinishedEvent(IEventHandler<CrTLoaderLoadingEvent.Finished> eventHandler) {
        if (Configuration.fastScriptLoading) {
            CraftTweakerAPI.logWarning("Fast script loading disabled registerLoadFinishedEvent. Please tell the mod author!");
        }
        tweaker.registerLoadFinishedEvent(eventHandler);
    }

    @Override
    public void registerLoadAbortedEvent(IEventHandler<CrTLoaderLoadingEvent.Aborted> eventHandler) {
        if (Configuration.fastScriptLoading) {
            CraftTweakerAPI.logWarning("Fast script loading disabled registerLoadAbortedEvent. Please tell the mod author!");
        }
        tweaker.registerLoadAbortedEvent(eventHandler);
    }

    @Override
    public void registerScriptLoadPreEvent(IEventHandler<CrTScriptLoadingEvent.Pre> eventHandler) {
        if (Configuration.fastScriptLoading) {
            CraftTweakerAPI.logWarning("Fast script loading disabled registerScriptLoadPreEvent. Please tell the mod author!");
        }
        tweaker.registerScriptLoadPreEvent(eventHandler);
    }

    @Override
    public void registerScriptLoadPostEvent(IEventHandler<CrTScriptLoadingEvent.Post> eventHandler) {
        if (Configuration.fastScriptLoading) {
            CraftTweakerAPI.logWarning("Fast script loading disabled registerScriptLoadPostEvent. Please tell the mod author!");
        }
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

    public void rollbackChanges() {
        while (!reloadableActions.isEmpty()) {
            ActionReloadCallback<?> action = reloadableActions.poll();
            try {
                action.undo();
            } catch (Throwable e) {
                CraftTweakerAPI.logError("Failed to undo action " + action.describeAction(), e);
            }
        }
    }

    public Queue<ActionReloadCallback<?>> getReloadableActions() {
        return reloadableActions;
    }

    public <T extends IAction> void addReloadCallback(Class<T> clazz, IActionReloadCallbackFactory<T> callback) {
        reloadCallbacks.put(clazz, callback);
    }

    public void clearLoaderTasks() {
        loaderTasks.clear();
        scriptDispatched = false;
    }

    public String getCurrentLoader() {
        return currentLoader;
    }

    private boolean validateAction(IAction action) {
        if (!action.validate()) {
            CraftTweakerAPI.logError("Action could not be applied", new UnsupportedOperationException(action.describeInvalid()));
            return true;
        }
        String describe;
        try {
            describe = action.describe();
        } catch (Throwable e) {
            describe = "Applying action " + action.getClass().getName() + "@" + action.hashCode() + " (failed to get its description)";
        }
        if (describe != null && !describe.isEmpty()) {
            CraftTweakerAPI.logInfo(describe);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private ActionReloadCallback<IAction> getReloadCallback(IAction action) {
        for (Class<?> clazz = action.getClass(); IAction.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            if (reloadCallbacks.containsKey(clazz)) {
                return ((IActionReloadCallbackFactory<IAction>) reloadCallbacks.get(clazz)).create(action);
            } else if (clazz.isAnnotationPresent(Reloadable.class)) {
                reloadCallbacks.put(clazz, AnnotatedActionReloadCallback::new);
                return new AnnotatedActionReloadCallback(action);
            }
        }
        return null;
    }

    private static void resetPrimitiveCastingRules() throws Exception {
        List<ZenType> primitiveTypes = ReflectUtils.getAllFieldsWithClass(ZenType.class, ZenType.class, null);
        Field castingRulesField = ZenType.class.getDeclaredField("castingRules");
        castingRulesField.setAccessible(true);
        for (ZenType primitiveType : primitiveTypes) {
            castingRulesField.set(primitiveType, null);
        }
    }

    private static void rebuildLiteralTypesCache() throws Exception {
        //noinspection JavaReflectionMemberAccess
        Field field = TypeRegistry.class.getField("literalTypes");
        Map<?, ?> literalTypes = (Map<?, ?>) field.get(GlobalRegistry.getTypes());
        literalTypes.clear();
    }

    private static String base62(long v) {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

        if (v == 0) {
            return "0";
        }

        char[] buf = new char[11]; // log_62(2^64) ~= 10.7
        int pos = buf.length;
        while (Long.compareUnsigned(v, 0) > 0) {
            long q = Long.divideUnsigned(v, 62);
            int r = (int) Long.remainderUnsigned(v, 62);
            buf[--pos] = chars[r];
            v = q;
        }
        return new String(buf, pos, buf.length - pos);
    }
}
