package youyihj.zenutils.impl.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stanhebben.zenscript.expression.ExpressionMap;
import stanhebben.zenscript.type.ZenTypeAssociative;
import youyihj.zenutils.impl.zenscript.IOrderlyType;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author youyihj
 */
@Mixin(value = ExpressionMap.class, remap = false)
public abstract class MixinExpressionMap {
    @Shadow
    @Final
    private ZenTypeAssociative type;

    @ModifyConstant(
            method = "compile",
            constant = @Constant(classValue = HashMap.class)
    )
    private Class<?> modifyCreateMapType(Class<?> clazz) {
        return ((IOrderlyType) type).isOrderly() ? LinkedHashMap.class : HashMap.class;
    }
}
