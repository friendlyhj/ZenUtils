package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stanhebben.zenscript.expression.partial.PartialType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeNative;

/**
 * @author youyihj
 */
@Mixin(value = PartialType.class, remap = false)
public abstract class MixinPartialType {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(method = "call", constant = @Constant(classValue = ZenTypeNative.class))
    private Class<?> alwaysTryCallingType(Object obj, Class<?> constant) {
        return ZenType.class;
    }
}
