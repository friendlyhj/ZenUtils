package youyihj.zenutils.processor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
public class HashCheck extends PreprocessorActionBase {

    public static final String PREPROCESSOR_NAME = "hashcheck";

    public HashCheck(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public String getPreprocessorName() {
        return PREPROCESSOR_NAME;
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        ZenUtils.enableHashCheck();
    }
}
