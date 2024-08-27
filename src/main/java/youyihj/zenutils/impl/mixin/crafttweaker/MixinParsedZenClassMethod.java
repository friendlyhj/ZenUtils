package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassMethod;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.mixin.MixinAnnotationTranslator;

import java.util.List;

/**
 * @author youyihj
 */
@Mixin(value = ParsedZenClassMethod.class, remap = false)
public abstract class MixinParsedZenClassMethod {
    @Shadow
    @Final
    ParsedFunction method;

    @Inject(method = "writeAll", at = @At("HEAD"))
    private void findAnnotation(
            ClassVisitor newClass, IEnvironmentClass environmentNewClass, CallbackInfo ci,
            @Share("isStatic") LocalBooleanRef isStatic, @Share("preprocessors") LocalRef<List<MixinPreprocessor>> mixinPreprocessors
    ) {
        List<MixinPreprocessor> preprocessors = MixinAnnotationTranslator.findAnnotation(method.getPosition());
        MixinPreprocessor staticAnnotation = null;
        for (MixinPreprocessor preprocessor : preprocessors) {
            if (preprocessor.getAnnotation().getLeft().equals("Static")) {
                staticAnnotation = preprocessor;
            }
        }
        isStatic.set(staticAnnotation != null);
        if (isStatic.get()) {
            preprocessors.remove(staticAnnotation);
        }
        mixinPreprocessors.set(preprocessors);
    }

    @ModifyArg(method = "writeAll", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/symbols/SymbolArgument;<init>(ILstanhebben/zenscript/type/ZenType;)V"))
    private int staticArgId(int id, @Share("isStatic") LocalBooleanRef isStatic) {
        return isStatic.get() ? id - 1 : id;
    }

    @ModifyConstant(
            method = "writeAll",
            constant = @Constant(intValue = Opcodes.ACC_PUBLIC),
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "NEW", target = "(Lstanhebben/zenscript/util/MethodOutput;Lstanhebben/zenscript/compiler/IEnvironmentClass;)Lstanhebben/zenscript/compiler/EnvironmentMethod;")
            ))
    private int modifyModifiers(int constant, @Share("isStatic") LocalBooleanRef isStatic, @Share("preprocessors") LocalRef<List<MixinPreprocessor>> mixinPreprocessors) {
        int accessModifier = Opcodes.ACC_PUBLIC;
        int staticModifier = 0;
        if (isStatic.get()) {
            staticModifier = Opcodes.ACC_STATIC;
        }
        for (MixinPreprocessor mixinPreprocessor : mixinPreprocessors.get()) {
            String type = mixinPreprocessor.getAnnotation().getLeft();
            if (type.equals("Inject") || type.equals("Redirect") || type.startsWith("Modify")) {
                accessModifier = Opcodes.ACC_PRIVATE;
            }
        }
        return accessModifier | staticModifier;
    }

    @Inject(method = "writeAll", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/util/MethodOutput;end()V"))
    private void applyAnnotation(ClassVisitor newClass, IEnvironmentClass environmentNewClass, CallbackInfo ci, @Local MethodOutput methodOutput, @Share("preprocessors") LocalRef<List<MixinPreprocessor>> mixinPreprocessors) {
        for (MixinPreprocessor mixinPreprocessor : mixinPreprocessors.get()) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            ZenPosition position = method.getPosition();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(),
                    methodOutput.getVisitor()::visitAnnotation,
                    it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
            );
        }
    }
}
