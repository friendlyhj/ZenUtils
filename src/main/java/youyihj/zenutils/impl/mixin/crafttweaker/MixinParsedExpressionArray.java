package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArray;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionArray;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.util.Arrays;

/**
 * @author youyihj
 */
@Mixin(value = ParsedExpressionArray.class, remap = false)
public abstract class MixinParsedExpressionArray extends ParsedExpression {
    public MixinParsedExpressionArray(ZenPosition position) {
        super(position);
    }

    @Inject(method = "compile", at = @At("RETURN"), cancellable = true)
    private void predictTypeAdvanced(IEnvironmentMethod environment, ZenType predictedType, CallbackInfoReturnable<IPartialExpression> cir, @Local Expression[] cContents) {
        IPartialExpression returnValue = cir.getReturnValue();
        if (!ZenType.ANYARRAY.equals(returnValue.getType()) || cContents.length == 0) {
            return;
        }
        ZenType[] contentTypes = new ZenType[cContents.length];
        ZenType predictedContentType = null;
        for (int i = 0; i < cContents.length; i++) {
            contentTypes[i] = cContents[i].getType();
        }
        predictedContentType = zu$predictArrayType(contentTypes, environment);
        if (predictedContentType != null) {
            for (int i = 0; i < cContents.length; i++) {
                cContents[i] = cContents[i].cast(getPosition(), environment, predictedContentType);
            }
            cir.setReturnValue(new ExpressionArray(getPosition(), new ZenTypeArrayBasic(predictedContentType), cContents));
        }
    }

    @Unique
    private ZenType zu$predictArrayType(ZenType[] contentTypes, IEnvironmentGlobal environment) {
        ZenType[] distinctTypes = Arrays.stream(contentTypes).distinct().toArray(ZenType[]::new);

        // if the content types are homogeneous
        if (distinctTypes.length == 1) {
            return distinctTypes[0];
        }

        // if the content types have the same super class
        Class<?> commonSuperclass = ReflectUtils.findCommonSuperclass(Arrays.stream(distinctTypes).map(ZenType::toJavaClass).toArray(Class[]::new));
        if (commonSuperclass != null) {
            return environment.getType(commonSuperclass);
        }

        // if the all remaining content types can cast to the first one
        ZenType first = distinctTypes[0];
        if (Arrays.stream(distinctTypes).skip(1).allMatch(type -> type.canCastImplicit(first, environment))) {
            return first;
        }
        return null;
    }
}
