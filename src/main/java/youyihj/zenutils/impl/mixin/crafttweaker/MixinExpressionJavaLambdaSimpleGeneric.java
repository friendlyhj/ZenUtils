package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenTypeUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author youyihj
 */
// TODO: now it fixes foo(T, T), how about foo(T1, T2)?
@Mixin(value = ExpressionJavaLambdaSimpleGeneric.class, remap = false)
public abstract class MixinExpressionJavaLambdaSimpleGeneric {
    @Shadow
    public Class<?> genericClass;

    @Shadow
    @Final
    private String descriptor;

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/ClassWriter;visitSource(Ljava/lang/String;Ljava/lang/String;)V"))
    private void lookupTypeVariable(boolean result, IEnvironmentMethod environment, CallbackInfo ci, @Local Method method, @Share("typeVariableIndices") LocalRef<int[]> typeVariableIndicesRef) {
        IntArrayList temp = new IntArrayList(method.getParameterCount());
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < genericParameterTypes.length; i++) {
            if (genericParameterTypes[i].getTypeName().equals("T")) {
                temp.add(i);
            }
        }
        typeVariableIndicesRef.set(temp.toIntArray());
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private boolean changeBridgeMethodGenerate(Object a, Object b, @Local Method method, @Local ClassWriter cw, @Local String clsName, @Share("typeVariableIndices") LocalRef<int[]> typeVariableIndicesRef) {
        MethodOutput bridge = new MethodOutput(cw, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE, method.getName(), ZenTypeUtil.descriptor(method), null, null);
        bridge.loadObject(0);
        int[] typeVariableIndices = typeVariableIndicesRef.get();
        int j = 0;
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (j < typeVariableIndices.length && typeVariableIndices[j] == i) {
                bridge.loadObject(i + 1);
                bridge.checkCast(ZenTypeUtil.internal(genericClass));
                j++;
            } else {
                bridge.load(org.objectweb.asm.Type.getType(method.getParameterTypes()[i]), i + 1);
            }
        }
        bridge.invokeVirtual(clsName, method.getName(), descriptor);
        bridge.returnType(org.objectweb.asm.Type.getReturnType(method));
        bridge.end();
        return true; // to skip origin code
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getDeclaredMethods()[Ljava/lang/reflect/Method;"))
    private Method[] useLambdaMethod(Class<?> clazz, @Local Method method) {
        return new Method[] {method};
    }
}
