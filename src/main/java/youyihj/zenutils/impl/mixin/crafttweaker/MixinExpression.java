package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionAs;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.CastingAnySubtype;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

/**
 * @author youyihj
 */
@Mixin(value = Expression.class, remap = false)
public abstract class MixinExpression implements IPartialExpression {
    @Inject(
            method = "cast",
            at = @At(
                    value = "INVOKE",
                    target = "Lstanhebben/zenscript/compiler/IEnvironmentGlobal;error(Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lstanhebben/zenscript/type/ZenType;canCastImplicit(Lstanhebben/zenscript/type/ZenType;Lstanhebben/zenscript/compiler/IEnvironmentGlobal;)Z"
                    )
            ),
            cancellable = true
    )
    private void javaNativeTypeCastExplicitly(ZenPosition position, IEnvironmentGlobal environment, ZenType type, CallbackInfoReturnable<Expression> cir) {
        if (type instanceof ZenTypeJavaNative && getType() instanceof ZenTypeJavaNative) {
            cir.setReturnValue(new ExpressionAs(position, (Expression) (Object) this, new CastingAnySubtype(getType(), type)));
        }
    }
}
