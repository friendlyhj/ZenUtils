package youyihj.zenutils.impl.zenscript.nat;

import com.google.gson.JsonElement;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.IPreprocessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author youyihj
 */
public class MixinAnnotationTranslator {
    private static final Map<String, Class<?>> SUPPORTED_ANNOTATIONS = new HashMap<>();
    private static void register(Class<?>... annotations) {
        for (Class<?> annotation : annotations) {
            SUPPORTED_ANNOTATIONS.put(annotation.getSimpleName(), annotation);
        }
    }

    static {
        register(
                Mixin.class,
                Final.class,
                Mutable.class,
                Overwrite.class,
                Pseudo.class,
                Shadow.class,
                SoftOverride.class,
                Unique.class,
                Inject.class,
                Group.class,
                ModifyArg.class,
                ModifyArgs.class,
                ModifyConstant.class,
                ModifyVariable.class,
                Redirect.class,
                Surrogate.class,

                At.class,
                Slice.class,
                Constant.class
        );
    }

    public static List<MixinPreprocessor> findAnnotation(ZenPosition position) {
        List<IPreprocessor> filePreprocessors = CraftTweakerAPI.tweaker.getPreprocessorManager().preprocessorActionsPerFile.get(position.getFileName());
        List<MixinPreprocessor> found = new ArrayList<>();
        Int2ObjectMap<MixinPreprocessor> mixinPreprocessors = new Int2ObjectOpenHashMap<>();
        for (IPreprocessor preprocessor : filePreprocessors) {
            if (preprocessor instanceof MixinPreprocessor) {
                MixinPreprocessor mixinPreprocessor = (MixinPreprocessor) preprocessor;
                mixinPreprocessors.put(mixinPreprocessor.getLineRange().getTo(), mixinPreprocessor);
            }
        }
        int line = position.getLine() - 2;
        while (true) {
            MixinPreprocessor mixinPreprocessor = mixinPreprocessors.get(line);
            if (mixinPreprocessor == null) {
                break;
            }
            found.add(0, mixinPreprocessor);
            line -= mixinPreprocessor.getPreprocessorLines().size();
        }
        return found;
    }

    public static void translate(String type, JsonElement json, BiFunction<String, Boolean, AnnotationVisitor> visitorPrimer, Function<String, ParseException> exceptionFactory) throws ParseException {
        Class<?> annotationType = SUPPORTED_ANNOTATIONS.get(type);
        if (annotationType == null) {
            throw exceptionFactory.apply("unsupported mixin annotation: " + type);
        }
        AnnotationVisitor visitor = visitorPrimer.apply(Type.getDescriptor(annotationType), isVisibleOnRuntime(annotationType));
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Class<?> expectedType;
            try {
                expectedType = annotationType.getMethod(key).getReturnType();
            } catch (NoSuchMethodException e) {
                throw exceptionFactory.apply(key + " does not exist in " + annotationType);
            }
            writeContentToAnnotation(visitor, expectedType, key, value, exceptionFactory);
        }
        visitor.visitEnd();
    }

    private static void writeContentToAnnotation(AnnotationVisitor visitor, Class<?> expectedType, String key, JsonElement value, Function<String, ParseException> exceptionFactory) {
        if (expectedType == int.class) {
            visitor.visit(key, value.getAsInt());
        } else if (expectedType == long.class) {
            visitor.visit(key, value.getAsLong());
        } else if (expectedType == float.class) {
            visitor.visit(key, value.getAsFloat());
        } else if (expectedType == double.class) {
            visitor.visit(key, value.getAsDouble());
        } else if (expectedType == boolean.class) {
            visitor.visit(key, value.getAsBoolean());
        } else if (expectedType == String.class) {
            visitor.visit(key, value.getAsString());
        } else if (expectedType == byte.class) {
            visitor.visit(key, value.getAsByte());
        } else if (expectedType == char.class) {
            visitor.visit(key, value.getAsCharacter());
        } else if (expectedType == short.class) {
            visitor.visit(key, value.getAsShort());
        } else if (expectedType == Class.class) {
            visitor.visit(key, Type.getObjectType(value.getAsString()));
        } else if (expectedType.isEnum()) {
            visitor.visitEnum(key, Type.getDescriptor(expectedType), value.getAsString());
        } else if (expectedType.isArray()) {
            AnnotationVisitor arrayVisitor = visitor.visitArray(key);
            if (value.isJsonArray()) {
                for (JsonElement content : value.getAsJsonArray()) {
                    writeContentToAnnotation(arrayVisitor, expectedType.getComponentType(), key, content, exceptionFactory);
                }
                arrayVisitor.visitEnd();
            } else {
                writeContentToAnnotation(arrayVisitor, expectedType.getComponentType(), key, value, exceptionFactory);
                arrayVisitor.visitEnd();
            }
        } else if (expectedType == At.class) {
            translate("At", value, (type, visible) -> visitor.visitAnnotation(key, type), exceptionFactory);
        } else if (expectedType == Redirect.class) {
            translate("Redirect", value, (type, visible) -> visitor.visitAnnotation(key, type), exceptionFactory);
        } else if (expectedType == Constant.class) {
            translate("Constant", value, (type, visible) -> visitor.visitAnnotation(key, type), exceptionFactory);
        }
    }

    private static boolean isVisibleOnRuntime(Class<?> annotationType) {
        return annotationType.getAnnotation(Retention.class).value() == RetentionPolicy.RUNTIME;
    }
}
