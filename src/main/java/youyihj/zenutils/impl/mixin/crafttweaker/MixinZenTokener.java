package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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

import java.util.Arrays;
import java.util.List;

import static stanhebben.zenscript.ZenTokener.T_QUEST;
import static youyihj.zenutils.impl.zenscript.ExtendZenTokens.*;

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
        IntArrayList finalList = new IntArrayList(FINALS);
        List<String> regexpsList = Lists.newArrayList(REGEXPS);
        finalList.add(T_TEMPLATE_STRING);
        finalList.add(T_ESCAPE_CHAR);
        finalList.addAll(finalList.indexOf(T_QUEST), IntArrayList.wrap(new int[] {T_QUEST2, T_QUEST_ASSIGN, T_QUEST_DOT}));
        regexpsList.add(T_TEMPLATE_STRING_REGEX);
        regexpsList.add(T_ESCAPE_CHAR_REGEX);
        regexpsList.addAll(regexpsList.indexOf("\\?"), Arrays.asList(T_QUEST2_REGEX, T_QUEST_ASSIGN_REGEX, T_QUEST_DOT_REGEX));
        FINALS = finalList.toIntArray();
        REGEXPS = regexpsList.toArray(new String[0]);
        DFA = new NFA(REGEXPS, FINALS).toDFA().optimize().compile();
        TemplateStringTokener.setupDFAFromZenTokener(REGEXPS, FINALS);
    }
}
