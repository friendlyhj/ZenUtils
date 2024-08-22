package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.type.*;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.LiteralType;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.nat.NativeClassValidate;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNativeIterable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    private final Map<String, ZenType> literalTypes = new HashMap<>();

    @Shadow
    public abstract ZenType getType(Type type);

    @Inject(method = "getType", at = @At(value = "HEAD"), cancellable = true)
    private void handleLiteralType(Type type, CallbackInfoReturnable<ZenType> cir) {
        if (type instanceof LiteralType) {
            cir.setReturnValue(literalTypes.computeIfAbsent(type.toString(), this::zu$handleLiteralType));
        }
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
                    return null;
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
                    return null;
                }
            }
        }
        if (name.startsWith("[")) {
            return new ZenTypeArrayBasic(getType(new LiteralType(name.substring(1))));
        }
        return new ZenTypeNative(Object.class);
    }

    @Unique
    private ZenTypeJavaNative zu$checkNative(ClassData classData) {
        if (NativeClassValidate.isValid(classData)) {
            return new ZenTypeJavaNative(classData, this);
        } else {
            return null;
        }
    }
}
