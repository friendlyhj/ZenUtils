package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.runtime.ScriptStatus;

/**
 * @author youyihj
 */
public class NotReloadablePreprocessor extends PreprocessorActionBase {
    public static final String NAME = "notreloadable";

    public NotReloadablePreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        if (InternalUtils.getScriptStatus() == ScriptStatus.RELOAD) {
            scriptFile.setCompileBlocked(true);
            scriptFile.setExecutionBlocked(true);
        }
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
