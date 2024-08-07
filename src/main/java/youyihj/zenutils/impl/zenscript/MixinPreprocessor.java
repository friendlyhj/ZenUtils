package youyihj.zenutils.impl.zenscript;

import crafttweaker.runtime.ScriptFile;
import stanhebben.zenscript.value.IntRange;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessor;

import java.util.List;

/**
 * @author youyihj
 */
public class MixinPreprocessor implements IMultilinePreprocessor {
    public static final String NAME = "mixin";

    private final String fileName;
    private final List<String> preprocessorLines;
    private final IntRange lineRange;

    public MixinPreprocessor(String fileName, List<String> preprocessorLines, IntRange lineRange) {
        this.fileName = fileName;
        this.preprocessorLines = preprocessorLines;
        this.lineRange = lineRange;
    }

    @Override
    public IntRange getLineRange() {
        return lineRange;
    }

    @Override
    public List<String> getPreprocessorLines() {
        return preprocessorLines;
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {

    }

    @Override
    public void executeActionOnFinish(ScriptFile scriptFile) {

    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
