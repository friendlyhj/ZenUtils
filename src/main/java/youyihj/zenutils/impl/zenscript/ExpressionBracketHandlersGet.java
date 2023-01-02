package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionBracketHandlersGet implements IPartialExpression {

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        return null;
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        return null;
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return null;
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        return null;
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return new ZenType[0];
    }

    @Override
    public IZenSymbol toSymbol() {
        return null;
    }

    @Override
    public ZenType getType() {
        return null;
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        return null;
    }
}
