package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stanhebben.zenscript.parser.expression.ParsedExpressionFunction;
import stanhebben.zenscript.type.ZenTypeNative;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

/**
 * @author youyihj
 */
@Mixin(value = ParsedExpressionFunction.class, remap = false)
public abstract class MixinParsedExpressionFunction {
    @Redirect(method = "compile", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0, shift = At.Shift.BEFORE))
    private boolean canUseNativeType(Object obj, Class<?> clazz) {
        return obj instanceof ZenTypeNative || obj instanceof ZenTypeJavaNative;
    }
}
