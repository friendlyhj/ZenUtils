package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.nat.CraftTweakerBridge;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeNative.class, remap = false)
public class MixinZenTypeNative {
    @Shadow @Final private Class<?> cls;

    @Inject(method = "constructCastingRules", at = @At("TAIL"))
    private void addToNativeCaster(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters, CallbackInfo ci) {
        CraftTweakerBridge.INSTANCE.getNativeCaster(cls).ifPresent(it ->
            rules.registerCastingRule(environment.getType(it.getReturnType()), new CastingRuleStaticMethod(new JavaMethod(it, environment)))
        );
    }



    @Inject(method = "getMember", at = @At(value = "HEAD"), cancellable = true)
    private void addToNativeMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, CallbackInfoReturnable<IPartialExpression> cir) {
        if ("native".equals(name)) {
            CraftTweakerBridge.INSTANCE.getNativeCaster(cls).ifPresent(it -> {
                cir.setReturnValue(new ExpressionCallStatic(position, environment, new JavaMethod(it, environment), value.eval(environment)));
            });
        }
    }
}
