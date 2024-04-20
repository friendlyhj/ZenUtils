package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

import java.util.List;

/**
 * @author youyihj
 */
public class ExpressionTemplateString extends Expression {
    private final List<ParsedExpression> expressions;

    public ExpressionTemplateString(ZenPosition position, List<ParsedExpression> expressions) {
        super(position);
        this.expressions = expressions;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        output.newObject(StringBuilder.class);
        output.dup();
        output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
        for (ParsedExpression expression : expressions) {
            expression.compile(environment, null).eval(environment)
                      .cast(getPosition(), environment, ZenType.STRING)
                      .compile(result, environment);
            output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
        }
        output.invokeVirtual(StringBuilder.class, "toString", String.class);
        if (!result) {
            output.pop();
        }
    }

    @Override
    public ZenType getType() {
        return ZenType.STRING;
    }
}
