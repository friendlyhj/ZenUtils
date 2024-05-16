package youyihj.zenutils.impl.mixin.crafttweaker;

import org.apache.commons.lang3.ArrayUtils;
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
import youyihj.zenutils.impl.zenscript.TemplateStringTokener;
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
        FINALS = ArrayUtils.addAll(FINALS, TemplateString.T_TEMPLATE_STRING, TemplateString.T_ESCAPE_CHAR);
        REGEXPS = ArrayUtils.addAll(REGEXPS, TemplateString.T_TEMPLATE_STRING_REGEX, TemplateString.T_ESCAPE_CHAR_REGEX);
        DFA = new NFA(REGEXPS, FINALS).toDFA().optimize().compile();
        TemplateStringTokener.setupDFAFromZenTokener(REGEXPS, FINALS);
    }
}
