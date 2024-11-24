package youyihj.zenutils.impl.zenscript;

import com.google.gson.*;
import crafttweaker.runtime.ScriptFile;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.value.IntRange;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class MixinPreprocessor implements IMultilinePreprocessor {
    public static final String NAME = "mixin";

    private final String fileName;
    private final List<String> preprocessorLines;
    private final IntRange lineRange;
    private Pair<String, JsonElement> annotation;

    private static final Gson GSON = new GsonBuilder().setLenient().create();

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
        if (preprocessorLines.size() != 1) {
            processMultiline(scriptFile);
        } else {
            processSingleLine(scriptFile);
        }
    }

    @Override
    public void executeActionOnFinish(ScriptFile scriptFile) {

    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public Pair<String, JsonElement> getAnnotation() {
        return annotation;
    }

    private void processMultiline(ScriptFile scriptFile) {
        String annotationType = getPreprocessorLine().trim().substring(NAME.length() + 1).trim();
        if (annotationType.isEmpty()) {
            annotationType = "Mixin";
        }
        String annotationContent = getPreprocessorLines().stream()
                .skip(1)
                .map(it -> it.trim().substring(1))
                .collect(Collectors.joining());
        if (!annotationContent.isEmpty()) {
            try {
                annotation = Pair.of(annotationType, GSON.fromJson(annotationContent, JsonElement.class));
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException(String.format("mixin annotation json is malformed, file: %s, line: [%d, %d]", scriptFile.getEffectiveName(), lineRange.getFrom(), lineRange.getTo()), e);
            }
        } else {
            annotation = Pair.of(annotationType, new JsonObject());
        }
    }

    private void processSingleLine(ScriptFile scriptFile) {
        String content = getPreprocessorLine().trim().substring(NAME.length() + 1).trim();
        int jsonStartIndex = content.indexOf('{');
        if (jsonStartIndex == -1) {
            jsonStartIndex = content.length();
        }
        String annotationType = content.substring(0, jsonStartIndex).trim();
        if (annotationType.isEmpty()) {
            annotationType = "Mixin";
        }
        String annotationContent = content.substring(jsonStartIndex).trim();
        if (!annotationContent.isEmpty()) {
            try {
                annotation = Pair.of(annotationType, GSON.fromJson(annotationContent, JsonElement.class));
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException(String.format("mixin annotation json is malformed, file: %s, line: %d", scriptFile.getEffectiveName(), getLineIndex()), e);
            }
        } else {
            annotation = Pair.of(annotationType, new JsonObject());
        }
    }
}
