package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.IPreprocessor;
import crafttweaker.preprocessor.PreprocessorFactory;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import stanhebben.zenscript.value.IntRange;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessor;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;
import youyihj.zenutils.impl.core.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author youyihj
 */
@Mixin(value = PreprocessorManager.class, remap = false)
public abstract class MixinPreprocessorManager {
    @Shadow
    private HashMap<String, PreprocessorFactory<?>> registeredPreprocessorActions;

    @Shadow
    protected abstract void addPreprocessorToFileMap(String filename, IPreprocessor preprocessor);

    @Shadow protected abstract void executePostActions(ScriptFile scriptFile);

    /**
     * @author youyihj
     * @reason read multiline processors
     */
    @Overwrite
    public List<IPreprocessor> checkFileForPreprocessors(ScriptFile scriptFile) {
        List<IPreprocessor> preprocessorList = new ArrayList<>();
        String filename = scriptFile.getName();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(scriptFile.open(), StandardCharsets.UTF_8));

            String line = "";
            String content = "";
            int lineIndex = -1;
            boolean readCurrentLineAgain = false;

            while (true) {
                if (!readCurrentLineAgain) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineIndex++;
                    content = line.trim();
                } else {
                    readCurrentLineAgain = false;
                }

                if (content.isEmpty()) {
                    continue;
                }

                if (Configuration.fastScriptLoading) {
                    // skip comments
                    if (content.startsWith("//")) {
                        continue;
                    }
                    if (content.startsWith("/*")) {
                        while ((line = reader.readLine()) != null) {
                            lineIndex++;
                            content = line.trim();
                            if (content.endsWith("*/")) {
                                break;
                            }
                        }
                        continue;
                    }
                }

                if (content.charAt(0) == '#') {
                    content = content.substring(1);
                    String[] splits = content.split(" ");
                    PreprocessorFactory<?> preprocessorFactory = registeredPreprocessorActions.get(splits[0]);
                    if (preprocessorFactory instanceof IMultilinePreprocessorFactory) {
                        IMultilinePreprocessorFactory<?> multilinePreprocessorFactory = (IMultilinePreprocessorFactory<?>) preprocessorFactory;
                        List<String> multilines = new ArrayList<>();
                        multilines.add(line);
                        int startIndex = lineIndex;
                        while ((line = reader.readLine()) != null) {
                            lineIndex++;
                            content = line.trim();
                            if (!content.isEmpty() && content.charAt(0) == '#') {
                                splits = content.substring(1).split(" ");
                                if (registeredPreprocessorActions.containsKey(splits[0])) {
                                    readCurrentLineAgain = true;
                                    break;
                                } else {
                                    multilines.add(line);
                                }
                            } else {
                                break;
                            }
                        }
                        IMultilinePreprocessor preprocessor = multilinePreprocessorFactory.createPreprocessor(scriptFile.getName(), multilines, new IntRange(startIndex, lineIndex - 1));
                        preprocessor.executeActionOnFind(scriptFile);
                        preprocessorList.add(preprocessor);
                        addPreprocessorToFileMap(scriptFile.getEffectiveName(), preprocessor);
                    } else if (preprocessorFactory != null) {
                        IPreprocessor preprocessor = preprocessorFactory.createPreprocessor(scriptFile.getName(), line, lineIndex);
                        preprocessor.executeActionOnFind(scriptFile);
                        preprocessorList.add(preprocessor);
                        addPreprocessorToFileMap(scriptFile.getEffectiveName(), preprocessor);
                    }
                } else if (Configuration.fastScriptLoading && !ArrayUtils.contains(scriptFile.getLoaderNames(), "mixin")) {
                    // fast script loading: skip preprocessor parsing in script body for non-mixin scripts
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            CraftTweakerAPI.logError("Could not read preprocessor functions in " + filename, e);
        }
        executePostActions(scriptFile);
        return preprocessorList;
    }
}
