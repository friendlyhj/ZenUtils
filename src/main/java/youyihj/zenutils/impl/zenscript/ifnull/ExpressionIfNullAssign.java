package youyihj.zenutils.impl.zenscript.ifnull;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionIfNullAssign extends Expression {
    private final IPartialExpression left;
    private final IPartialExpression right;

    public ExpressionIfNullAssign(ZenPosition position, IPartialExpression left, IPartialExpression right) {
        super(position);
        this.left = left;
        this.right = right;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        left.eval(environment).compile(true, environment);
        Label lblExit = new Label();
        output.ifNonNull(lblExit);
        left.assign(getPosition(), environment, right.eval(environment).cast(getPosition(), environment, left.getType())).compile(false, environment);
        output.label(lblExit);
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }
}
