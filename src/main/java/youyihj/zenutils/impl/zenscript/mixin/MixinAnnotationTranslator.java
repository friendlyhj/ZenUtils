package youyihj.zenutils.impl.zenscript.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.IPreprocessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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
                Desc.class,

                ModifyExpressionValue.class,
                ModifyReceiver.class,
                ModifyReturnValue.class,
                WrapWithCondition.class,
                WrapMethod.class,
                WrapOperation.class,

                At.class,
                Slice.class,
                Constant.class,

                Cancellable.class,
                Local.class,
                Share.class,

                Definition.class,
                Expression.class
        );
    }

    public static List<MixinPreprocessor> findAnnotation(ZenPosition position) {
        List<IPreprocessor> filePreprocessors = CraftTweakerAPI.tweaker.getPreprocessorManager().preprocessorActionsPerFile.get(position.getFileName());
        List<MixinPreprocessor> found = new ArrayList<>();
        if (filePreprocessors == null) {
            return found;
        }
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

    public static void translate(String type, JsonElement json, ZenPosition position, BiFunction<String, Boolean, AnnotationVisitor> visitorPrimer) throws ParseException {
        Class<?> annotationType = SUPPORTED_ANNOTATIONS.get(type);
        if (annotationType == null) {
            throw error("unsupported mixin annotation: " + type, position);
        }
        AnnotationVisitor visitor = visitorPrimer.apply(Type.getDescriptor(annotationType), isVisibleOnRuntime(annotationType));
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Class<?> expectedType;
            try {
                expectedType = annotationType.getMethod(key).getReturnType();
            } catch (NoSuchMethodException e) {
                throw error(key + " does not exist in " + annotationType, position);
            }
            writeContentToAnnotation(visitor, expectedType, key, value, position);
        }
        processAnnotation(annotationType, visitor, json.getAsJsonObject(), position);
        visitor.visitEnd();
    }

    public static List<String> getMixinTargets(JsonObject json) {
        List<String> targets = new ArrayList<>();
        if (json.has("targets")) {
            JsonElement targetsJson = json.get("targets");
            if (targetsJson.isJsonArray()) {
                for (JsonElement target : targetsJson.getAsJsonArray()) {
                    targets.add(target.getAsString());
                }
            } else {
                targets.add(targetsJson.getAsString());
            }
        }
        if (json.has("value")) {
            JsonElement valueJson = json.get("value");
            if (valueJson.isJsonArray()) {
                for (JsonElement target : valueJson.getAsJsonArray()) {
                    targets.add(target.getAsString().replace('/', '.'));
                }
            } else {
                targets.add(valueJson.getAsString().replace('/', '.'));
            }
        }
        return targets;
    }

    private static void writeContentToAnnotation(AnnotationVisitor visitor, Class<?> expectedType, String key, JsonElement value, ZenPosition position) {
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
                    writeContentToAnnotation(arrayVisitor, expectedType.getComponentType(), key, content, position);
                }
                arrayVisitor.visitEnd();
            } else {
                writeContentToAnnotation(arrayVisitor, expectedType.getComponentType(), key, value, position);
                arrayVisitor.visitEnd();
            }
        } else if (expectedType.isAnnotation()) {
            translate(expectedType.getSimpleName(), value, position, (type, visible) -> visitor.visitAnnotation(key, type));
        }
    }

    private static boolean isVisibleOnRuntime(Class<?> annotationType) {
        return annotationType.getAnnotation(Retention.class).value() == RetentionPolicy.RUNTIME;
    }

    private static void processAnnotation(Class<?> annotationType, AnnotationVisitor visitor, JsonObject json, ZenPosition position) throws ParseException {
        if (InternalUtils.hasMethod(annotationType, "remap")) {
            visitor.visit("remap", false);
            if (json.has("remap")) {
                throw error("remap always is false", position);
            }
        }
    }

    private static ParseException error(String message, ZenPosition position) {
        return new ParseException(position.getFile(), position.getLine() - 1, 0, message);
    }

    public static class RepeatableAnnotationHelper {
        private final List<Pair<ZenPosition, JsonElement>> elements = new ArrayList<>();
        private final Class<?> repeatableContainer;
        private final Class<?> annotationType;

        public RepeatableAnnotationHelper(String type) {
            Class<?> annotationType = SUPPORTED_ANNOTATIONS.get(type);
            this.annotationType = annotationType;
            if (annotationType == null || !annotationType.isAnnotationPresent(Repeatable.class)) {
                throw new IllegalArgumentException("unsupported mixin repeatable annotation: " + type);
            }
            repeatableContainer = annotationType.getAnnotation(Repeatable.class).value();
        }

        public void add(ZenPosition position, JsonElement element) {
            elements.add(Pair.of(position, element));
        }

        public void writeAll(BiFunction<String, Boolean, AnnotationVisitor> visitorPrimer) throws ParseException {
            if (elements.isEmpty()) {
                return;
            }
            AnnotationVisitor visitor = visitorPrimer.apply(Type.getDescriptor(repeatableContainer), isVisibleOnRuntime(repeatableContainer));
            String valueKey = "value";
            AnnotationVisitor arrayVisitor = visitor.visitArray(valueKey);
            for (Pair<ZenPosition, JsonElement> element : elements) {
                writeContentToAnnotation(arrayVisitor, annotationType, valueKey, element.getRight(), element.getLeft());
            }
            arrayVisitor.visitEnd();
            visitor.visitEnd();
        }
    }
}
