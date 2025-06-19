package youyihj.zenutils.impl.zenscript.ifnull;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ParsedExpressionIfNotNullElse extends ParsedExpression {
    private final ParsedExpression value;
    private final ParsedExpression onElse;

    public ParsedExpressionIfNotNullElse(ZenPosition position, ParsedExpression value, ParsedExpression onElse) {
        super(position);
        this.value = value;
        this.onElse = onElse;
    }

    @Override
    public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
        Expression cValue = value.compile(environment, predictedType).eval(environment);
        if (!cValue.getType().isPointer()) {
            environment.error(getPosition(), "primitive types can not be null");
            return cValue;
        }
        return new ExpressionIfNotNullElse(getPosition(), cValue, onElse.compile(environment, predictedType).eval(environment));
    }
}
