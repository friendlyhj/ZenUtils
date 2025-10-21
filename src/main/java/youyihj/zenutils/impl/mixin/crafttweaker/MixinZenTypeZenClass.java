package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.nat.ExpressionClassConstant;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeZenClass.class, remap = false)
public abstract class MixinZenTypeZenClass {
    @Shadow
    public abstract Type toASMType();

    /**
     * @author youyihj
     * @reason wrong impl
     */
    @Overwrite
    public boolean isPointer() {
        return true;
    }

    @Inject(method = "getStaticMember", at = @At("HEAD"), cancellable = true)
    private void injectClassStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name, CallbackInfoReturnable<IPartialExpression> cir) {
        if (name.equals("class")) {
            cir.setReturnValue(new ExpressionClassConstant(position, toASMType()));
        }
    }
}
