package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.ParsedExpressionDeepNull;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNotNullElse;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNotNullMember;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNullAssign;

import static stanhebben.zenscript.ZenTokener.*;
import static youyihj.zenutils.impl.zenscript.ExtendZenTokens.*;

/**
 * @author youyihj
 */
@Mixin(value = ParsedExpression.class, remap = false)
public abstract class MixinParsedExpression {
    @Shadow
    private static ParsedExpression readAssignExpression(ZenTokener parser, IEnvironmentGlobal environment) {
        throw new AssertionError();
    }


    @ModifyReturnValue(
            method = "readPrimaryExpression",
            at = @At("RETURN"),
            slice = @Slice(
                from = @At(
                        value = "INVOKE",
                        target = "Lstanhebben/zenscript/IZenCompileEnvironment;getBracketed(Lstanhebben/zenscript/compiler/IEnvironmentGlobal;Ljava/util/List;)Lstanhebben/zenscript/symbols/IZenSymbol;"
                ),
                to = @At(
                        value = "INVOKE",
                        target = "Lstanhebben/zenscript/parser/expression/ParsedExpressionInvalid;<init>(Lstanhebben/zenscript/util/ZenPosition;)V",
                        shift = At.Shift.BY,
                        by = 2
                )
            )
    )
    private static ParsedExpression changeDeepNullExpression(ParsedExpression original) {
        return new ParsedExpressionDeepNull(original.getPosition());
    }

    /*
    Pseudo Mixin, see CraftTweakerMixinPlugin for implementation code
    @AddSwitchBranch(method = "readPrimaryExpression", key = TemplateString.T_BACKQUOTE, cancellable = true)
    private static void readTemplateString(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment, CallbackInfoReturnable<ParsedExpression> cir) {
        cir.setReturnValue(TemplateString.getExpression(parser, position, environment));
    }
     */

    @Inject(
            method = "readPostfixExpression",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lstanhebben/zenscript/ZenTokener;peek()Lstanhebben/zenscript/parser/Token;"
            )
    )
    private static void handleIfNullSugar(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment, CallbackInfoReturnable<ParsedExpression> cir, @Local LocalRef<ParsedExpression> base) {
        Token token = parser.peek();
        if (token == null) {
            return;
        }
        switch (token.getType()) {
            // a ?? b -> !isNull(a) ? a : b
            case T_QUEST2:
                parser.next();
                base.set(new ParsedExpressionIfNotNullElse(
                        position,
                        base.get(),
                        readAssignExpression(parser, environment)
                ));
                break;
            // a ?= b -> if (isNull(a)) { a = b; }
            case T_QUEST_ASSIGN:
                parser.next();
                base.set(new ParsedExpressionIfNullAssign(
                        position,
                        base.get(),
                        readAssignExpression(parser, environment)
                ));
                break;
            // a?.member -> !isNull(a) ? a.member : null
            case T_QUEST_DOT:
                parser.next();
                String memberName;
                Token indexString = parser.optional(T_ID, T_VERSION, T_STRING);
                if (indexString != null) {
                    memberName = indexString.getValue();
                } else {
                    indexString = parser.optional(T_STRINGVALUE);
                    if (indexString != null) {
                        memberName = StringUtil.unescapeString(indexString.getValue());
                    } else {
                        Token last = parser.next();
                        throw new ParseException(last, "Invalid expression, last token: " + last.getValue());
                    }
                }
                base.set(new ParsedExpressionIfNotNullMember(position, base.get(), memberName));
                break;
            // how about other operators?
            // a ?+ b -> !isNull(a) ? a + b : null (?)
            // a?[i] -> !isNull(a) ? a[i] : null (??)
            // a?[i] = b -> if (!isNull(a)) { a[i] = b; } (???)
        }
    }
}
