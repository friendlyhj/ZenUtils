package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.impl.util.InternalUtils;

public class HardFailPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "hardfail";

    public HardFailPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        InternalUtils.hardFailMode = true;
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
