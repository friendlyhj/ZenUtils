package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionSuper extends Expression {
    private final ZenTypeJavaNativeSuper superType;

    public ExpressionSuper(ZenPosition position, ZenTypeJavaNative type) {
        super(position);
        this.superType = type.toSuper();
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (result) {
            environment.getOutput().loadObject(0);
        }
    }

    @Override
    public ZenType getType() {
        return superType;
    }
}
