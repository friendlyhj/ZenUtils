package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.ParsedExpressionDeepNull;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNotNullElse;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNotNullMember;
import youyihj.zenutils.impl.zenscript.ifnull.ParsedExpressionIfNullAssign;

import static stanhebben.zenscript.ZenTokener.*;
import static youyihj.zenutils.impl.zenscript.ExtendZenTokens.T_QUEST2;
import static youyihj.zenutils.impl.zenscript.ExtendZenTokens.T_QUEST_ASSIGN;

/**
 * @author youyihj
 */
@Mixin(value = ParsedExpression.class, remap = false)
public abstract class MixinParsedExpression {
    @Shadow
    private static ParsedExpression readAssignExpression(ZenTokener parser, IEnvironmentGlobal environment) {
        throw new AssertionError();
    }

    @Shadow
    private static ParsedExpression readOrOrExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
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
    @AddSwitchBranch(method = "readPrimaryExpression", key = T_BACKQUOTE, cancellable = true)
    private static void readTemplateString(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment, CallbackInfoReturnable<ParsedExpression> cir) {
        cir.setReturnValue(TemplateString.getExpression(parser, position, environment));
    }
     */

    @Inject(
            method = "readAssignExpression",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void handleNullishCoalescingAssignment(ZenTokener parser, IEnvironmentGlobal environment, CallbackInfoReturnable<ParsedExpression> cir) {
        Token token = parser.optional(T_QUEST_ASSIGN);
        if (token != null) {
            cir.setReturnValue(new ParsedExpressionIfNullAssign(token.getPosition(), cir.getReturnValue(), readAssignExpression(parser, environment)));
        }
    }

    @Redirect(
            method = "readConditionalExpression",
            at = @At(
                    value = "INVOKE",
                    target = "Lstanhebben/zenscript/parser/expression/ParsedExpression;readOrOrExpression(Lstanhebben/zenscript/util/ZenPosition;Lstanhebben/zenscript/ZenTokener;Lstanhebben/zenscript/compiler/IEnvironmentGlobal;)Lstanhebben/zenscript/parser/expression/ParsedExpression;"
            )
    )
    private static ParsedExpression handleNullishCoalescingOperator(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
        ParsedExpression left = readOrOrExpression(position, parser, environment);

        while (parser.optional(T_QUEST2) != null) {
            ParsedExpression right = readOrOrExpression(parser.peek().getPosition(), parser, environment);
            left = new ParsedExpressionIfNotNullElse(position, left, right);
        }

        return left;
    }

    @ReflectionInvoked(asm = true)
    private static ParsedExpression handleOptionalChaining(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment, ParsedExpression base) {
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
        return new ParsedExpressionIfNotNullMember(position, base, memberName);
    }
}
