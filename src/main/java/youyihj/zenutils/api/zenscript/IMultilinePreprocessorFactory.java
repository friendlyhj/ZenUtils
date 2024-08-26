package youyihj.zenutils.api.zenscript;

import crafttweaker.preprocessor.PreprocessorFactory;
import stanhebben.zenscript.value.IntRange;

import java.util.Collections;
import java.util.List;

/**
 * @author youyihj
 */
public interface IMultilinePreprocessorFactory<R extends IMultilinePreprocessor> extends PreprocessorFactory<R> {
    R createPreprocessor(String fileName, List<String> preprocessorLines, IntRange lineRange);

    @Override
    default R createPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        return createPreprocessor(fileName, Collections.singletonList(preprocessorLine), new IntRange(lineIndex, lineIndex));
    }
}
