package youyihj.zenutils.impl.reload;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptLoader;
import youyihj.zenutils.api.preprocessor.ReloadablePreprocessor;

import java.util.List;

/**
 * @author youyihj
 */
public class ScriptReloader {
    private static final String SCRIPT_LOADER_NAME = ReloadablePreprocessor.NAME;
    private static final List<String> reloadableLoaders = Lists.newArrayList(SCRIPT_LOADER_NAME);

    public static void addReloadableLoader(String loaderName) {
        if (!reloadableLoaders.contains(loaderName)) {
            reloadableLoaders.add(loaderName);
        }
    }

    public static List<String> getReloadableLoaders() {
        return reloadableLoaders;
    }

    public static boolean reloadScripts() {
        ScriptLoader loader = new ScriptLoader(reloadableLoaders.toArray(new String[0]));
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        return loader.getLoaderStage() != ScriptLoader.LoaderStage.ERROR;
    }
}
