package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import stanhebben.zenscript.util.ZenTypeUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeUtil.class, remap = false)
public abstract class MixinZenTypeUtil {
    @WrapOperation(method = "findFunctionalInterfaceMethod", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Method;isDefault()Z"))
    private static boolean skipObjectMethods(Method instance, Operation<Boolean> original) {
        if (instance.getName().equals("equals") && Arrays.equals(instance.getParameterTypes(), new Class[]{Object.class})) {
            return true;
        }
        if (instance.getName().equals("hashCode") && instance.getParameterCount() == 0) {
            return true;
        }
        if (instance.getName().equals("toString") && instance.getParameterCount() == 0) {
            return true;
        }
        return original.call(instance);
    }
}
