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
public class PartialMapClear implements IPartialExpression {
    private final ZenPosition position;
    private final Expression map;

    public PartialMapClear(ZenPosition position, Expression map) {
        this.position = position;
        this.map = map;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid expression");
        return null;
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        environment.error(position, "cannot alter this final");
        return null;
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "methods have no members");
        return null;
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        if (values.length != 0) {
            environment.error(position, "clear method requires no arguments");
            return null;
        }
        return new ExpressionMapClear(position, map);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return new ZenType[0];
    }

    @Override
    public IZenSymbol toSymbol() {
        return position1 -> new PartialMapClear(position1, map);
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid type");
        return null;
    }
}
