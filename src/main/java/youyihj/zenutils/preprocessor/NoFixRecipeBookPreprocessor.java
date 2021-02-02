package youyihj.zenutils.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.util.ReflectUtils;

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
        CraftTweakerAPI.logInfo("no fix recipe book preprocessor is found in " + scriptFile + ". Don't fix the recipe book");
        try {
            ReflectUtils.removePrivateFinal(CraftTweaker.class, "alreadyChangedThePlayer").set(null, true);
        } catch (Exception e) {
            CraftTweakerAPI.logError("fail to disable fixing recipe book", e);
        }
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
