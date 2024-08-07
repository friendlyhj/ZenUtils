package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import crafttweaker.preprocessor.IPreprocessor;
import crafttweaker.preprocessor.PreprocessorFactory;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.ScriptFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.value.IntRange;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessor;
import youyihj.zenutils.api.zenscript.IMultilinePreprocessorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author youyihj
 */
@Mixin(value = PreprocessorManager.class, remap = false)
public abstract class MixinPreprocessorManager {
    @Shadow private HashMap<String, PreprocessorFactory<?>> registeredPreprocessorActions;

    @Shadow protected abstract void addPreprocessorToFileMap(String filename, IPreprocessor preprocessor);

    @Inject(
            method = "checkLine",
            at = @At(
                    value = "INVOKE",
                    target = "Lcrafttweaker/preprocessor/IPreprocessor;executeActionOnFind(Lcrafttweaker/runtime/ScriptFile;)V"
            ),
            cancellable = true
    )
    private void denyPreprocessingWhenCheckingSingleLine(ScriptFile scriptFile, String line, int lineIndex, CallbackInfoReturnable<IPreprocessor> cir, @Local IPreprocessor preprocessor) {
        cir.setReturnValue(preprocessor);
    }

    @WrapOperation(
            method = "checkFileForPreprocessors",
            at = @At(
                    value = "INVOKE",
                    target = "Lcrafttweaker/preprocessor/PreprocessorManager;checkLine(Lcrafttweaker/runtime/ScriptFile;Ljava/lang/String;I)Lcrafttweaker/preprocessor/IPreprocessor;"
            )
    )
    private IPreprocessor readMultilinePreprocessor(PreprocessorManager instance, ScriptFile scriptFile, String line, int lineIndex, Operation<IPreprocessor> original, @Local BufferedReader reader, @Local LocalIntRef mutLineIndex) throws IOException {
        IPreprocessor preprocessor = original.call(instance, scriptFile, line, lineIndex);
        if (preprocessor instanceof IMultilinePreprocessor) {
            List<String> lines = Lists.newArrayList(preprocessor.getPreprocessorLine());
            String multiline;
            while (true) {
                multiline = reader.readLine();
                if (multiline == null || !multiline.trim().startsWith("#")) {
                    break;
                }
                mutLineIndex.set(mutLineIndex.get() + 1);
                lines.add(multiline);
            }
            IMultilinePreprocessorFactory<?> multilinePreprocessorFactory = (IMultilinePreprocessorFactory<?>) registeredPreprocessorActions.get(preprocessor.getPreprocessorName());
            preprocessor = multilinePreprocessorFactory.createPreprocessor(preprocessor.getFileName(), lines, new IntRange(lineIndex, mutLineIndex.get()));
        }
        if (preprocessor != null) {
            preprocessor.executeActionOnFind(scriptFile);
            addPreprocessorToFileMap(scriptFile.getName(), preprocessor);
        }
        return preprocessor;
    }
}
