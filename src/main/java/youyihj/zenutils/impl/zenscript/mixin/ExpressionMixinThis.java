package youyihj.zenutils.impl.zenscript.mixin;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.LiteralType;

/**
 * @author youyihj
 */
public class ExpressionMixinThis extends Expression {
    private final String mixinTarget;
    private final IEnvironmentGlobal environment;

    public ExpressionMixinThis(ZenPosition position, String mixinTarget, IEnvironmentGlobal environment) {
        super(position);
        this.mixinTarget = mixinTarget;
        this.environment = environment;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (result) {
            MethodOutput output = environment.getOutput();
            output.loadObject(0);
            output.checkCast("java/lang/Object");
            output.checkCast(mixinTarget);
        }
    }

    @Override
    public ZenType getType() {
        return environment.getType(new LiteralType("L" + mixinTarget + ";"));
    }
}
