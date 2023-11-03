package youyihj.zenutils.api.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.logger.LogLevel;
import youyihj.zenutils.api.logger.ScriptSuppressLogFilter;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author youyihj
 */
public class SuppressErrorPreprocessor extends PreprocessorActionBase {
    public static final String NAME = "suppress";
    public final Set<LogLevel> suppressLevels = EnumSet.noneOf(LogLevel.class);

    public SuppressErrorPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
        String[] levelsString = preprocessorLine.substring(NAME.length() + 1).trim().split(" +");
        for (String levelString : levelsString) {
            LogLevel suppressLevel;
            levelString = levelString.toUpperCase();
            if (levelString.charAt(levelString.length() - 1) == 'S') {
                levelString = levelString.substring(0, levelString.length() - 1);
            }
            if (levelString.equals("ALL")) {
                suppressLevel = LogLevel.FATAL;
            } else {
                try {
                    suppressLevel = LogLevel.valueOf(levelString);
                } catch (IllegalArgumentException e) {
                    CraftTweakerAPI.logError("No such log level: " + levelString);
                    continue;
                }
            }
            suppressLevels.add(suppressLevel);
        }
    }

    @Override
    public void executeActionOnFind(ScriptFile scriptFile) {
        ZenUtils.crafttweakerLogger.getLogOption().addFilter(new ScriptSuppressLogFilter(scriptFile.getName(), suppressLevels));
    }

    @Override
    public String getPreprocessorName() {
        return NAME;
    }
}
