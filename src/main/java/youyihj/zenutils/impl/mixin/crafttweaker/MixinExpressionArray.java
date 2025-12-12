package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArray;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.util.Objects;

/**
 * @author youyihj
 */
@Mixin(value = ExpressionArray.class, remap = false)
public abstract class MixinExpressionArray extends Expression {
    public MixinExpressionArray(ZenPosition position) {
        super(position);
    }

    @Inject(method = "cast", at = @At("HEAD"), cancellable = true)
    private void allowCastToObject(ZenPosition position, IEnvironmentGlobal environment, ZenType type, CallbackInfoReturnable<Expression> cir) {
        if (Objects.equals(type.toJavaClass(), Object.class)) {
            cir.setReturnValue(this);
        }
    }
}
