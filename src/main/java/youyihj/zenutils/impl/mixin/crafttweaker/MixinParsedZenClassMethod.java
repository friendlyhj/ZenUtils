package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClassMethod;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
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

    @Shadow
    @Final
    String className;

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
    private int modifyModifiers(int constant, @Share("isStatic") LocalBooleanRef isStatic, @Share("preprocessors") LocalRef<List<MixinPreprocessor>> mixinPreprocessors, @Share("isSynthetic") LocalBooleanRef isSynthetic) {
        int accessModifier = Opcodes.ACC_PUBLIC;
        int staticModifier = 0;
        int syntheticModifier = 0;
        if (isStatic.get()) {
            staticModifier = Opcodes.ACC_STATIC;
            syntheticModifier = Opcodes.ACC_SYNTHETIC;
        }
        for (MixinPreprocessor mixinPreprocessor : mixinPreprocessors.get()) {
            String type = mixinPreprocessor.getAnnotation().getLeft();
            if (type.equals("Inject") || type.equals("Redirect") || type.startsWith("Modify") || type.startsWith("Wrap")) {
                accessModifier = Opcodes.ACC_PRIVATE;
                syntheticModifier = 0;
            }
            if (type.equals("Shadow")) {
                syntheticModifier = 0;
            }
        }
        if (syntheticModifier != 0) {
            isSynthetic.set(true);
        }
        return accessModifier | staticModifier | syntheticModifier;
    }

    @Inject(method = "writeAll", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/util/MethodOutput;end()V"))
    private void applyAnnotation(ClassVisitor newClass, IEnvironmentClass environmentNewClass, CallbackInfo ci, @Local MethodOutput methodOutput, @Share("preprocessors") LocalRef<List<MixinPreprocessor>> mixinPreprocessors, @Share("isSynthetic") LocalBooleanRef isSynthetic, @Share("isStatic") LocalBooleanRef isStatic) {
        // can we give a method with more than 64 parameters?
        long refSet = 0L;
        for (MixinPreprocessor mixinPreprocessor : mixinPreprocessors.get()) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            String annotationName = annotation.getLeft();
            JsonObject annotationBody = annotation.getRight().getAsJsonObject();
            if (("Local".equals(annotationName) && JsonUtils.getBoolean(annotationBody, "ref", false))
                    || "Share".equals(annotationName)) {
                int parameterIndex = JsonUtils.getInt(annotationBody, "parameter", -1);
                int actualParameterIndex = parameterIndex >= 0 ? parameterIndex : method.getArguments().size() + parameterIndex;
                if (!(method.getArguments().get(actualParameterIndex).getType() instanceof ZenTypeArrayBasic)) {
                    throw new ParseException(method.getPosition().getFile(), method.getPosition().getLine() - 1, 0, "Array type expected for LocalRef representation");
                }
                refSet |= 1L << actualParameterIndex;
            }
        }
        if (refSet == 0L) {
            generatePlainMixinMethod(mixinPreprocessors.get(), methodOutput);
        } else {
            generateRefMixinMethod(refSet, mixinPreprocessors.get(), newClass, isStatic.get());
        }
        if (isSynthetic.get()) {
            methodOutput.getVisitor().visitAnnotation("Lyouyihj/zenutils/impl/member/VisibleSynthetic;", true).visitEnd();
        }
    }

    @Unique
    private void generatePlainMixinMethod(List<MixinPreprocessor> mixinPreprocessors, MethodOutput methodOutput) {
        for (MixinPreprocessor mixinPreprocessor : mixinPreprocessors) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            String annotationName = annotation.getLeft();
            JsonObject annotationBody = annotation.getRight().getAsJsonObject();
            ZenPosition position = method.getPosition();
            if ("Cancellable".equals(annotationName) || "Local".equals(annotationName)) {
                int parameterIndex = JsonUtils.getInt(annotationBody, "parameter", -1);
                annotationBody.remove("parameter");
                annotationBody.remove("ref");
                MixinAnnotationTranslator.translate(
                        annotationName, annotationBody,
                        (name, visible) -> methodOutput.getVisitor().visitParameterAnnotation(parameterIndex >= 0 ? parameterIndex : method.getArguments().size() + parameterIndex, name, visible),
                        it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
                );
            } else {
                MixinAnnotationTranslator.translate(
                        annotationName, annotationBody,
                        methodOutput.getVisitor()::visitAnnotation,
                        it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
                );
            }
        }

    }

    @Unique
    private void generateRefMixinMethod(long refSet, List<MixinPreprocessor> mixinPreprocessors, ClassVisitor newClass, boolean isStatic) {
        List<ParsedFunctionArgument> arguments = method.getArguments();
        int acc = Opcodes.ACC_PRIVATE;
        if (isStatic) {
            acc |= Opcodes.ACC_STATIC;
        }
        StringBuilder descriptorBuilder = new StringBuilder();
        StringBuilder signatureBuilder = new StringBuilder();
        descriptorBuilder.append('(');
        signatureBuilder.append('(');
        for (int i = 0; i < arguments.size(); i++) {
            String signature = arguments.get(i).getType().getSignature();
            if ((refSet & (1L << i)) != 0) {
                ZenType baseType = ((ZenTypeArrayBasic) arguments.get(i).getType()).getBaseType();
                if (baseType == ZenType.INT) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalIntRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalIntRef;");
                } else if (baseType == ZenType.BOOL) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;");
                } else if (baseType == ZenType.FLOAT) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalFloatRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalFloatRef;");
                } else if (baseType == ZenType.LONG) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalLongRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalLongRef;");
                } else if (baseType == ZenType.DOUBLE) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalDoubleRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalDoubleRef;");
                } else if (baseType == ZenType.SHORT) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalShortRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalShortRef;");
                } else if (baseType == ZenType.BYTE) {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalByteRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalByteRef;");
                } else {
                    descriptorBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalRef;");
                    signatureBuilder.append("Lcom/llamalad7/mixinextras/sugar/ref/LocalRef<");
                    signatureBuilder.append(baseType.getSignature());
                    signatureBuilder.append(">;");
                }
            } else {
                descriptorBuilder.append(signature);
                signatureBuilder.append(signature);
            }
        }
        descriptorBuilder.append(')');
        signatureBuilder.append(')');
        descriptorBuilder.append(method.getReturnType().getSignature());
        signatureBuilder.append(method.getReturnType().getSignature());

        MethodOutput methodOutput = new MethodOutput(
                newClass,
                acc,
                method.getName() + "$ref$bridge",
                descriptorBuilder.toString(),
                signatureBuilder.toString(),
                null
        );
        generatePlainMixinMethod(mixinPreprocessors, methodOutput);
        methodOutput.start();
        if (!isStatic) {
            methodOutput.loadObject(0);
        }
        int argIndex = isStatic ? 0 : 1;
        short[] localArrayIndexes = new short[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            ZenType type = arguments.get(i).getType();
            if ((refSet & (1L << i)) != 0) {
                ZenType baseType = ((ZenTypeArrayBasic) type).getBaseType();
                methodOutput.iConst1();
                methodOutput.newArray(baseType.toASMType());
                int local = methodOutput.local(type.toASMType());
                localArrayIndexes[i] = (short) local;
                methodOutput.store(type.toASMType(), local);
                methodOutput.load(type.toASMType(), local);
                methodOutput.iConst0();
                methodOutput.load(type.toASMType(), argIndex);
                argIndex += type.toASMType().getSize();
                if (baseType == ZenType.INT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalIntRef", "get", "()I");
                } else if (baseType == ZenType.BOOL) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalBooleanRef", "get", "()Z");
                } else if (baseType == ZenType.FLOAT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalFloatRef", "get", "()F");
                } else if (baseType == ZenType.LONG) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalLongRef", "get", "()J");
                } else if (baseType == ZenType.DOUBLE) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalDoubleRef", "get", "()D");
                } else if (baseType == ZenType.SHORT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalShortRef", "get", "()S");
                } else if (baseType == ZenType.BYTE) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalByteRef", "get", "()B");
                } else {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalRef", "get", "()Ljava/lang/Object;");
                    methodOutput.checkCast(baseType.toASMType().getInternalName());
                }
                methodOutput.arrayStore(baseType.toASMType());
                methodOutput.load(type.toASMType(), local);
            } else {
                methodOutput.load(type.toASMType(), argIndex);
                argIndex += type.toASMType().getSize();
            }
        }

        if (isStatic) {
            methodOutput.invokeStatic(className, method.getName(), method.getSignature());
        } else {
            methodOutput.invokeVirtual(className, method.getName(), method.getSignature());
        }

        Type returnType = method.getReturnType().toASMType();
        int returnValueLocal = -1;
        if (method.getReturnType() != ZenType.VOID) {
            returnValueLocal = methodOutput.local(returnType);
            methodOutput.store(returnType, returnValueLocal);
        }

        argIndex = isStatic ? 0 : 1;
        for (int i = 0; i < arguments.size(); i++) {
            ZenType type = arguments.get(i).getType();
            if ((refSet & (1L << i)) != 0) {
                ZenType baseType = ((ZenTypeArrayBasic) type).getBaseType();
                methodOutput.loadObject(argIndex);
                methodOutput.loadObject(localArrayIndexes[i]);
                methodOutput.iConst0();
                methodOutput.arrayLoad(baseType.toASMType());
                if (baseType == ZenType.INT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalIntRef", "set", "(I)V");
                } else if (baseType == ZenType.BOOL) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalBooleanRef", "set", "(Z)V");
                } else if (baseType == ZenType.FLOAT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalFloatRef", "set", "(F)V");
                } else if (baseType == ZenType.LONG) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalLongRef", "set", "(J)V");
                } else if (baseType == ZenType.DOUBLE) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalDoubleRef", "set", "(D)V");
                } else if (baseType == ZenType.SHORT) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalShortRef", "set", "(S)V");
                } else if (baseType == ZenType.BYTE) {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalByteRef", "set", "(B)V");
                } else {
                    methodOutput.invokeInterface("com/llamalad7/mixinextras/sugar/ref/LocalRef", "set", "(Ljava/lang/Object;)V");
                }
            }
            argIndex += type.toASMType().getSize();
        }

        if (returnValueLocal != -1) {
            methodOutput.load(returnType, returnValueLocal);
            methodOutput.returnType(returnType);
        } else {
            methodOutput.ret();
        }

        methodOutput.end();
    }
}
