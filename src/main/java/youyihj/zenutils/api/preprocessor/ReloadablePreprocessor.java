package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;

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
        scriptFile.setLoaderNames(ArrayUtils.add(scriptFile.getLoaderNames(), NAME));
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
