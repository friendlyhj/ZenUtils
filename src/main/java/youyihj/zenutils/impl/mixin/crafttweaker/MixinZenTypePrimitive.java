package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

/**
 * @author youyihj
 */
@Mixin(value = {
        ZenTypeBool.class,
        ZenTypeByte.class,
        ZenTypeShort.class,
        ZenTypeInt.class,
        ZenTypeLong.class,
        ZenTypeFloat.class,
        ZenTypeDouble.class
}, remap = false)
public abstract class MixinZenTypePrimitive extends ZenType {
    @Shadow(remap = false)
    public abstract int getNumberType();

    @Inject(method = "constructCastingRules", at = @At("TAIL"))
    private void addCastToObjectRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters, CallbackInfo ci) {
        switch (getNumberType()) {
            case 0: // bool
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(BOOL_VALUEOF));
                break;
            case NUM_BYTE:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(BYTE_VALUEOF));
                break;
            case NUM_SHORT:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(SHORT_VALUEOF));
                break;
            case NUM_INT:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(INT_VALUEOF));
                break;
            case NUM_LONG:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(LONG_VALUEOF));
                break;
            case NUM_FLOAT:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(FLOAT_VALUEOF));
                break;
            case NUM_DOUBLE:
                rules.registerCastingRule(ZenTypeJavaNative.OBJECT, new CastingRuleStaticMethod(DOUBLE_VALUEOF));
                break;
        }
    }
}
