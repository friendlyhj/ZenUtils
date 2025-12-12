package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionMap;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.IOrderlyType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author youyihj
 */
@Mixin(value = ExpressionMap.class, remap = false)
public abstract class MixinExpressionMap extends Expression {
    @Shadow
    @Final
    private ZenTypeAssociative type;

    public MixinExpressionMap(ZenPosition position) {
        super(position);
    }

    @ModifyConstant(
            method = "compile",
            constant = @Constant(classValue = HashMap.class)
    )
    private Class<?> modifyCreateMapType(Class<?> clazz) {
        return ((IOrderlyType) type).isOrderly() ? LinkedHashMap.class : HashMap.class;
    }

    @Inject(method = "cast", at = @At("HEAD"), cancellable = true)
    private void allowCastToObject(ZenPosition position, IEnvironmentGlobal environment, ZenType type, CallbackInfoReturnable<Expression> cir) {
        if (Objects.equals(type.toJavaClass(), Object.class)) {
            cir.setReturnValue(this);
        }
    }
}
