package youyihj.zenutils.api.logger;

import crafttweaker.CraftTweakerAPI;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public class ScriptPosition {
    private final String fileName;
    private final int line;

    public ScriptPosition(String fileName, int line) {
        this.fileName = fileName;
        this.line = line;
    }

    @Nullable
    public static ScriptPosition current() {
        String scriptFileAndLine = CraftTweakerAPI.getScriptFileAndLine();
        if ("<?>".equals(scriptFileAndLine)) {
            return null;
        } else {
            String[] split = scriptFileAndLine.split(":");
            return new ScriptPosition(split[0], Integer.parseInt(split[1]));
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return fileName + ":" + line;
    }
}
