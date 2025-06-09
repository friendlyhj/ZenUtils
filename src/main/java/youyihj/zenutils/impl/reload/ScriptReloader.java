package youyihj.zenutils.impl.reload;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptLoader;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.logger.LogLevel;
import youyihj.zenutils.api.preprocessor.ReloadablePreprocessor;
import youyihj.zenutils.impl.runtime.ZenUtilsLogger;

import java.util.List;
import java.util.Set;

/**
 * @author youyihj
 */
public class ScriptReloader {
    private static final String SCRIPT_LOADER_NAME = ReloadablePreprocessor.NAME;
    private static final List<String> reloadableLoaders = Lists.newArrayList(SCRIPT_LOADER_NAME);
    private static final Set<String> UNRELOADABLE_LOADERS = ImmutableSet.of("preinit", "contenttweaker", "mixin");

    public static void addReloadableLoader(String loaderName) {
        if (UNRELOADABLE_LOADERS.contains(loaderName)) {
            CraftTweakerAPI.logError("The loader '" + loaderName + "' is not reloadable.");
            return;
        }
        if (!reloadableLoaders.contains(loaderName)) {
            reloadableLoaders.add(loaderName);
        }
    }

    public static List<String> getReloadableLoaders() {
        return reloadableLoaders;
    }

    // go to ReloadCommand#reloadScripts
    /* package-private */ static boolean reloadScripts() {
        ScriptLoader loader = new ScriptLoader(reloadableLoaders.toArray(new String[0]));
        ZenUtilsLogger logger = ZenUtils.crafttweakerLogger;
        logger.clear();
        logger.getLogOption().setMinLogLevel(LogLevel.WARNING);
        CraftTweakerAPI.tweaker.loadScript(true, loader);
        if (loader.getLoaderStage() == ScriptLoader.LoaderStage.ERROR || logger.hasError()) {
            return false;
        }
        loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
        logger.clear();
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        return loader.getLoaderStage() != ScriptLoader.LoaderStage.ERROR;
    }
}
