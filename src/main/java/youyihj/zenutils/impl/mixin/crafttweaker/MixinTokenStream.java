package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stanhebben.zenscript.parser.TokenStream;
import youyihj.zenutils.impl.zenscript.ITokenStreamExtension;

/**
 * @author youyihj
 */
@Mixin(value = TokenStream.class, remap = false)
public abstract class MixinTokenStream implements ITokenStreamExtension {
    @Unique
    private boolean allowWhitespaceChannel;

    @Override
    public void setAllowWhitespaceChannel(boolean allowWhitespaceChannel) {
        this.allowWhitespaceChannel = allowWhitespaceChannel;
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @ModifyConstant(method = "advance", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO))
    private int skipWhitespaceCheck(int constant) {
        return allowWhitespaceChannel ? Integer.MIN_VALUE : 0;
    }
}
