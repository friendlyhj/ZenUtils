package youyihj.zenutils.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.type.ZenType;
import youyihj.zenutils.impl.zenscript.IOrderlyType;

/**
 * @author youyihj
 */
@Mixin(value = ZenType.class, remap = false)
public class MixinZenType {
    @Inject(method = "read", at = @At("RETURN"))
    private static void readOrderlyTag(ZenTokener tokener, IEnvironmentGlobal environment, CallbackInfoReturnable<ZenType> cir) {
        ZenType returnValue = cir.getReturnValue();
        if (returnValue instanceof IOrderlyType) {
            if (tokener.optional(ZenTokener.T_DOLLAR) != null) {
                Token orderlyToken = tokener.required(ZenTokener.T_ID, "orderly required");
                if ("orderly".equals(orderlyToken.getValue())) {
                    ((IOrderlyType) returnValue).setOrderly();
                } else {
                    environment.error("orderly required");
                }
            }
        }
    }
}
