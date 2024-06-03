package youyihj.zenutils.api.preprocessor;

import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.impl.zenscript.nat.MCPReobfuscation;

/**
 * @author youyihj
 */
public class DownloadReobfMappingPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "download_reobf_mapping";

    public DownloadReobfMappingPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        MCPReobfuscation.INSTANCE.init();
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
