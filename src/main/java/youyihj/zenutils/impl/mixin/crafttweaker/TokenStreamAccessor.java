package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import stanhebben.zenscript.parser.TokenStream;

/**
 * @author youyihj
 */
@Mixin(value = TokenStream.class, remap = false)
public interface TokenStreamAccessor {
    @Accessor
    void setLine(int line);

    @Accessor
    void setLineOffset(int lineOffset);
}
