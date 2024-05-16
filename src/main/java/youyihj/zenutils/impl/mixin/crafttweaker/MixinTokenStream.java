package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stanhebben.zenscript.parser.TokenStream;
import youyihj.zenutils.impl.zenscript.TemplateStringTokener;

/**
 * @author youyihj
 */
@Mixin(value = TokenStream.class, remap = false)
public abstract class MixinTokenStream {
    @SuppressWarnings({"MixinAnnotationTarget", "ConstantValue", "EqualsBetweenInconvertibleTypes"})
    @ModifyConstant(method = "advance", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO))
    private int skipWhitespaceCheck(int constant) {
        return this.getClass().equals(TemplateStringTokener.class) ? Integer.MIN_VALUE : 0;
    }
}
