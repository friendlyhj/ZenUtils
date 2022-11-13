package youyihj.zenutils.impl.zenscript.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.ExpansionManager;
import youyihj.zenutils.impl.zenscript.ParsedExpansion;
import youyihj.zenutils.impl.zenscript.PartialCallExpansionMethod;

/**
 * @author youyihj
 */
@Mixin(value = ZenType.class, remap = false)
public class MixinZenType {
    @Inject(method = "memberExpansion", at = @At(value = "RETURN"), cancellable = true)
    private void toExpansionMethod(ZenPosition position, IEnvironmentGlobal environment, Expression value, String member, CallbackInfoReturnable<IPartialExpression> cir) {
        if (cir.getReturnValue() == null) {
            ParsedExpansion expansion = ExpansionManager.getExpansion(((ZenType) (Object) this), member);
            if (expansion != null) {
                ParsedFunction fn = expansion.getFunction();
                cir.setReturnValue(new PartialCallExpansionMethod(position, expansion.getOwner().getClassName(), fn.getName(), fn.getSignature(), fn.getArguments(), fn.getReturnType(), value));
            }
        }
    }
}
