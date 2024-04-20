package youyihj.zenutils.impl.zenscript;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

import java.util.List;

/**
 * @author youyihj
 */
public class ExpressionTemplateString extends Expression {
    private final String formatString;
    private final List<ParsedExpression> expressions;

    public ExpressionTemplateString(ZenPosition position, String formatString, List<ParsedExpression> expressions) {
        super(position);
        this.formatString = formatString;
        this.expressions = expressions;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        output.constant(formatString);
        output.constant(expressions.size());
        output.newArray(Type.getType(String.class));
        for (int i = 0; i < expressions.size(); i++) {
            ParsedExpression expression = expressions.get(i);
            output.dup();
            output.constant(i);
            expression.compile(environment, null).eval(environment)
                      .cast(getPosition(), environment, ZenType.STRING)
                      .compile(result, environment);
            output.arrayStore(Type.getType(String.class));
        }
        output.invokeStatic(String.class, "format", String.class, String.class, Object[].class);
        if (!result) {
            output.pop();
        }
    }

    @Override
    public ZenType getType() {
        return ZenType.STRING;
    }
}
