package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.IOrderlyType;
import youyihj.zenutils.impl.zenscript.PartialMapClear;
import youyihj.zenutils.impl.zenscript.PartialMapRemove;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeAssociative.class, remap = false)
public abstract class MixinZenTypeAssociate extends ZenType implements IOrderlyType {
    private boolean orderly;

    @Override
    public boolean isOrderly() {
        return orderly;
    }

    @Override
    public void setOrderly() {
        this.orderly = true;
    }

    @Inject(method = "getMember", at = @At("HEAD"), cancellable = true)
    private void addRemoveMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, CallbackInfoReturnable<IPartialExpression> cir) {
        if (name.equals("remove")) {
            cir.setReturnValue(new PartialMapRemove(position, value.eval(environment), (ZenTypeAssociative) (Object) this));
        } else if (name.equals("clear")) {
            cir.setReturnValue(new PartialMapClear(position, value.eval(environment)));
        }
    }
}
