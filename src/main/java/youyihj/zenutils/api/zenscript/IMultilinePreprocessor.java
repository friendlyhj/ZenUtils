package youyihj.zenutils.api.zenscript;

import crafttweaker.preprocessor.IPreprocessor;
import stanhebben.zenscript.value.IntRange;

import java.util.List;

/**
 * @author youyihj
 */
public interface IMultilinePreprocessor extends IPreprocessor {
    IntRange getLineRange();

    List<String> getPreprocessorLines();

    @Override
    default int getLineIndex() {
        return getLineRange().getFrom();
    }

    @Override
    default String getPreprocessorLine() {
        return getPreprocessorLines().get(0);
    }
}
