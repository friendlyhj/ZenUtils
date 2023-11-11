package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.runtime.HardFailLogger;

public class HardFailPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "hardfail";
    private static boolean used;

    public HardFailPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        if (!used) {
            ZenUtils.crafttweakerLogger.addLogger(new HardFailLogger());
            used = true;
        }
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
