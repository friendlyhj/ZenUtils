package youyihj.zenutils.api.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.impl.util.ReflectUtils;

/**
 * @author youyihj
 */
public class NoFixRecipeBookPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "no_fix_recipe_book";
    private static boolean called = false;

    public NoFixRecipeBookPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        if (!called) {
            called = true;
            CraftTweakerAPI.logInfo("no fix recipe book preprocessor is found in " + scriptFile + ". Don't fix the recipe book.");
        } else {
            CraftTweakerAPI.logWarning("Detected duplicated no fix recipe book preprocessor calling. You should call the preprocessor only once!");
            return;
        }
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
