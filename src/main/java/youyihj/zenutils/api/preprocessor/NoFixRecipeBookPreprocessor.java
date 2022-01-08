package youyihj.zenutils.api.preprocessor;

import crafttweaker.CraftTweakerAPI;
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
        CraftTweakerAPI.logWarning("no_fix_recipe_book preprocessor is deprecated now!");
        CraftTweakerAPI.logWarning("You should install no recipe book mod instead. (https://www.curseforge.com/minecraft/mc-mods/no-recipe-book)");
        CraftTweakerAPI.logWarning("CraftTweaker detects this mod. If the mod is found, CraftTweaker won't fix the recipe book.");
        CraftTweaker.alreadyChangedThePlayer = true;
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
