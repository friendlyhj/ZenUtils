package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import crafttweaker.util.SuppressErrorFlag;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
public class SuppressErrorPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "suppress";
    public final SuppressErrorFlag suppressLevel;

    public SuppressErrorPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
        switch (preprocessorLine.substring(NAME.length() + 1).trim()) {
            case "warning":
            case "warnings":
                suppressLevel = SuppressErrorFlag.ONLY_WARNINGS;
                break;
            case "errors":
            case "error":
            case "all":
                suppressLevel = SuppressErrorFlag.ALL;
                break;
            default:
                suppressLevel = SuppressErrorFlag.DEFAULT;
        }
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        InternalUtils.doSuppressErrorSingleScriptMode();
        InternalUtils.putSuppressErrorFlag(scriptFile.getEffectiveName(), suppressLevel);
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
