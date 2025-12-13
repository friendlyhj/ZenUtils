package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class PartialMapRemove implements IPartialExpression {
    private final ZenPosition position;
    private final Expression map;
    private final ZenTypeAssociative mapType;

    public PartialMapRemove(ZenPosition position, Expression map, ZenTypeAssociative mapType) {
        this.position = position;
        this.map = map;
        this.mapType = mapType;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid expression");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        environment.error(position, "cannot alter this final");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "methods have no members");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        if (values.length != 1) {
            environment.error(position, "remove method requires exactly one argument");
            return new ExpressionInvalid(position);
        }
        Expression keyExpr = values[0];
        return new ExpressionMapRemove(position, map, keyExpr.cast(position, environment, mapType.getKeyType()), mapType);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return new ZenType[] {mapType.getKeyType()};
    }

    @Override
    public IZenSymbol toSymbol() {
        return position1 -> new PartialMapRemove(position1, map, mapType);
    }

    @Override
    public ZenType getType() {
        return mapType.getValueType();
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid type");
        return ZenType.ANY;
    }
}
