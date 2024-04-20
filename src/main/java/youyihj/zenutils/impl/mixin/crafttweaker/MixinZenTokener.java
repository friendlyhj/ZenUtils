package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.parser.CompiledDFA;
import stanhebben.zenscript.parser.NFA;
import stanhebben.zenscript.util.ArrayUtil;
import youyihj.zenutils.impl.zenscript.TemplateString;

/**
 * @author youyihj
 */
@Mixin(value = ZenTokener.class, remap = false)
public abstract class MixinZenTokener {
    @Shadow
    @Final
    @Mutable
    private static String[] REGEXPS;

    @Shadow
    @Final
    @Mutable
    private static int[] FINALS;

    @Shadow
    @Final
    @Mutable
    private static CompiledDFA DFA;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void zu$addTemplateStringToken(CallbackInfo ci) {
        FINALS = ArrayUtil.add(FINALS, TemplateString.TOKEN_ID);
        REGEXPS = ArrayUtil.add(REGEXPS, TemplateString.TOKEN_REGEX);
        DFA = new NFA(REGEXPS, FINALS).toDFA().optimize().compile();
    }
}
