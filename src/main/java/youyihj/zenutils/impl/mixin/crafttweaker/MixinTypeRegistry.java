package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.type.*;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.LiteralType;
import youyihj.zenutils.impl.member.bytecode.MethodParameterParser;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.nat.NativeClassValidate;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeClass;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNativeIterable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@Mixin(value = TypeRegistry.class, remap = false)
public abstract class MixinTypeRegistry implements ITypeRegistry {
    @Shadow
    @Final
    private Map<Class<?>, ZenType> types;

    private final Map<String, ZenType> predefinedTypes = InternalUtils.make(new HashMap<>(), map -> {
        map.put("Ljava/lang/Object;", ZenTypeJavaNative.OBJECT);
        map.put("Ljava/lang/String;", ZenType.STRING);
        map.put("Ljava/lang/Class;", ZenTypeClass.INSTANCE);
        map.put("Ljava/lang/Boolean;", ZenType.BOOLOBJECT);
        map.put("Ljava/lang/Integer;", ZenType.INTOBJECT);
        map.put("Ljava/lang/Long;", ZenType.LONGOBJECT);
        map.put("Ljava/lang/Float;", ZenType.FLOATOBJECT);
        map.put("Ljava/lang/Double;", ZenType.DOUBLEOBJECT);
        map.put("Ljava/lang/Byte;", ZenType.BYTEOBJECT);
        map.put("Ljava/lang/Short;", ZenType.SHORTOBJECT);
        map.put("I", ZenType.INT);
        map.put("Z", ZenType.BOOL);
        map.put("J", ZenType.LONG);
        map.put("F", ZenType.FLOAT);
        map.put("D", ZenType.DOUBLE);
        map.put("B", ZenType.BYTE);
        map.put("S", ZenType.SHORT);
        map.put("V", ZenType.VOID);
    });

    public final Map<String, ZenType> literalTypes = new HashMap<>();

    @Shadow
    public abstract ZenType getType(Type type);

    @Shadow public abstract ZenType getClassType(Class cls);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerClassType(CallbackInfo ci) {
        types.put(Class.class, ZenTypeClass.INSTANCE);
    }

    @Inject(method = "getType", at = @At(value = "HEAD"), cancellable = true)
    private void handleComplexTypes(Type type, CallbackInfoReturnable<ZenType> cir) {
        if (type instanceof LiteralType) {
            if (predefinedTypes.containsKey(type.toString())) {
                cir.setReturnValue(predefinedTypes.get(type.toString()));
            } else if (literalTypes.containsKey(type.toString())) {
                cir.setReturnValue(literalTypes.get(type.toString()));
            } else {
                ZenType zenLiteralType = zu$handleLiteralType(type.toString());
                literalTypes.put(type.toString(), zenLiteralType);
                cir.setReturnValue(zenLiteralType);
            }
        }
        if (type instanceof TypeVariable) {
            cir.setReturnValue(getType(((TypeVariable<?>) type).getBounds()[0]));
        }
        if (type instanceof GenericArrayType) {
            cir.setReturnValue(new ZenTypeArrayBasic(getType(((GenericArrayType) type).getGenericComponentType())));
        }
    }

    @ModifyReturnValue(method = "getType", at = @At("RETURN"), slice = @Slice(
            from = @At(value = "CONSTANT", args = "nullValue=true")
    ))
    private ZenType modifyTypeFallback(ZenType original) {
        return ZenTypeJavaNative.OBJECT;
    }

    @Inject(method = "getClassType", at = @At(value = "NEW", target = "stanhebben/zenscript/type/ZenTypeNative"), cancellable = true)
    private void redirectToJavaNative(Class<?> cls, CallbackInfoReturnable<ZenType> cir) {
        ClassData classData = InternalUtils.getClassDataFetcher().forClass(cls);
        if (NativeClassValidate.isValid(classData)) {
            cir.setReturnValue(new ZenTypeJavaNative(classData, this));
            types.put(cls, cir.getReturnValue());
        }
    }

    @Inject(
            method = "getType",
            at = @At(
                    value = "INVOKE",
                    target = "Lstanhebben/zenscript/compiler/TypeRegistry;getClassType(Ljava/lang/Class;)Lstanhebben/zenscript/type/ZenType;",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void getIterableType(Type type, CallbackInfoReturnable<ZenType> cir, @Local ParameterizedType pType, @Local Class<?> rawClass) {
        ClassDataFetcher classDataFetcher = InternalUtils.getClassDataFetcher();
        if (classDataFetcher.forClass(Iterable.class).isAssignableFrom(classDataFetcher.forClass(rawClass))) {
            cir.setReturnValue(new ZenTypeJavaNativeIterable(classDataFetcher.forClass(rawClass), getType(pType.getActualTypeArguments()[0]), this));
        }
    }

    @Unique
    private ZenType zu$handleLiteralType(String name) {
        ClassDataFetcher classDataFetcher = InternalUtils.getClassDataFetcher();
        if (name.startsWith("L")) {
            if (!name.contains("<")) {
                String className = name.substring(1, name.length() - 1).replace('/', '.');
                try {
                    ClassData classData = classDataFetcher.forName(className);
                    return zu$checkNative(classData);
                } catch (ClassNotFoundException e) {
                    return ZenTypeJavaNative.OBJECT;
                }
            } else {
                try {
                    String genericInfo = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    String rawClassName = name.substring(1, name.indexOf("<")).replace('/', '.');
                    //TODO: handle nested generic type
                    String[] genericTypes = genericInfo.split(",");
                    ClassData rawClassData = classDataFetcher.forName(rawClassName);
                    if (genericTypes.length == 1) {
                        if (classDataFetcher.forClass(List.class).isAssignableFrom(rawClassData)) {
                            return new ZenTypeArrayList(getType(new LiteralType(genericTypes[0])));
                        }
                    }
                    if (genericTypes.length == 2) {
                        if (classDataFetcher.forClass(Map.class).isAssignableFrom(rawClassData)) {
                            return new ZenTypeAssociative(getType(new LiteralType(genericTypes[1])), getType(new LiteralType(genericTypes[0])));
                        }
                    }
                    return zu$checkNative(rawClassData);
                } catch (ClassNotFoundException e) {
                    return ZenTypeJavaNative.OBJECT;
                }
            }
        }
        if (name.startsWith("[")) {
            return new ZenTypeArrayBasic(getType(new LiteralType(name.substring(1))));
        }
        return ZenTypeJavaNative.OBJECT;
    }

    @Unique
    private ZenType zu$checkNative(ClassData classData) {
        if (NativeClassValidate.isValid(classData)) {
            return new ZenTypeJavaNative(classData, this);
        } else if (classData.isAnnotationPresent(ZenClass.class)) {
            String zenName = classData.getAnnotation(ZenClass.class).value();
            for (ZenType value : types.values()) {
                if (value.getName().equals(zenName)) {
                    return value;
                }
            }
            try {
                return getClassType(Class.forName(classData.name()));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return ZenTypeJavaNative.OBJECT;
    }

    /**
     * @author youyihj
     * @reason allow zs to use List subclasses
     */
    @Overwrite
    private ZenType getListType(ParameterizedType type) {
        return new ZenTypeArrayList(getType(type.getActualTypeArguments()[0]));
    }

    /**
     * @author youyihj
     * @reason allow zs to use Map subclasses
     */
    @Overwrite
    private ZenType getMapType(ParameterizedType type) {
        return new ZenTypeAssociative(getType(type.getActualTypeArguments()[1]), getType(type.getActualTypeArguments()[0]));
    }
}
