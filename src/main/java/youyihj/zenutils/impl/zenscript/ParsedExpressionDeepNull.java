package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ParsedExpressionDeepNull extends ParsedExpression {
    public ParsedExpressionDeepNull(ZenPosition position) {
        super(position);
    }

    @Override
    public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
        return new ExpressionDeepNull(getPosition());
    }
}
