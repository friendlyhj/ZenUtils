package youyihj.zenutils.impl.zenscript.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.ZenTokener;
import youyihj.zenutils.impl.zenscript.ExpansionKeyword;

import java.util.HashMap;

/**
 * @author youyihj
 */
@Mixin(value = ZenTokener.class, remap = false)
public class MixinZenTokener {
    @Shadow @Final private static HashMap<String, Integer> KEYWORDS;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void putExpansionKeyword(CallbackInfo ci) {
        KEYWORDS.put(ExpansionKeyword.KEYWORD, ExpansionKeyword.ID);
    }
}
