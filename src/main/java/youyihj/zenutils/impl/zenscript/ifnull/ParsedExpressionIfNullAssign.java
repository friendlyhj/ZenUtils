package youyihj.zenutils.impl.zenscript.ifnull;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionNothing;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ParsedExpressionIfNullAssign extends ParsedExpression {
    private final ParsedExpression left;
    private final ParsedExpression right;

    public ParsedExpressionIfNullAssign(ZenPosition position, ParsedExpression left, ParsedExpression right) {
        super(position);
        this.left = left;
        this.right = right;
    }

    @Override
    public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
        IPartialExpression cLeft = left.compile(environment, predictedType);
        IPartialExpression cRight = right.compile(environment, cLeft.getType());
        if (!cLeft.getType().isPointer()) {
            environment.error(getPosition(), "primitive types can not be null");
            return new ExpressionNothing(getPosition());
        }
        return new ExpressionIfNullAssign(getPosition(), cLeft, cRight);
    }
}
