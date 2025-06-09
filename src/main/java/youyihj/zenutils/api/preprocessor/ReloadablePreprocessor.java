package youyihj.zenutils.api.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import youyihj.zenutils.impl.core.Configuration;

/**
 * @author youyihj
 */
public class ReloadablePreprocessor extends PreprocessorActionBase {
    public static final String NAME = "reloadable";

    public ReloadablePreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        if (!Configuration.disableMixinScriptReloadWarning && ArrayUtils.contains(scriptFile.getLoaderNames(), "mixin")) {
            CraftTweakerAPI.logWarning(scriptFile.getEffectiveName() + " is marked as reloadable, but it is also a mixin script. The content of mixin script can not be refreshed during runtime, reloading it will cause issues. But feel free to make a static lib script. If you are sure all the 'reloadable' mixin scripts are libs, you can disable the warning by setting `disableMixinScriptReloadWarning` to true in zenutils config.");
        }
        scriptFile.setLoaderNames(ArrayUtils.add(scriptFile.getLoaderNames(), NAME));
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
