package youyihj.zenutils.api.preprocessor;

import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;

/**
 * @author youyihj
 */
public class NoFixRecipeBookPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "no_fix_recipe_book";

    public NoFixRecipeBookPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        CraftTweaker.alreadyChangedThePlayer = true;
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
