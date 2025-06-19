package youyihj.zenutils.impl.zenscript.ifnull;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.ZenTypeDeepNull;

/**
 * @author youyihj
 */
public class ExpressionIfNotNullElse extends Expression {
    private final Expression value;
    private final Expression onElse;

    public ExpressionIfNotNullElse(ZenPosition position, Expression value, Expression onElse) {
        super(position);
        this.value = value;
        this.onElse = onElse;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (!result) {
            return;
        }
        MethodOutput output = environment.getOutput();
        Label lblExit = new Label();
        value.compile(true, environment);
        output.dup();
        output.ifNonNull(lblExit);
        onElse.cast(getPosition(), environment, getType()).compile(true, environment);
        output.swap();
        output.pop();
        output.label(lblExit);
    }

    @Override
    public ZenType getType() {
        ZenType type = value.getType();
        return type == ZenType.ANY || type == ZenTypeDeepNull.INSTANCE ? onElse.getType() : type;
    }
}
