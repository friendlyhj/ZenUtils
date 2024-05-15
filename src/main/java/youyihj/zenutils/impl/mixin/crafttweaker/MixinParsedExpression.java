package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import youyihj.zenutils.impl.zenscript.ParsedExpressionDeepNull;

/**
 * @author youyihj
 */
@Mixin(value = ParsedExpression.class, remap = false)
public abstract class MixinParsedExpression {
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
}
