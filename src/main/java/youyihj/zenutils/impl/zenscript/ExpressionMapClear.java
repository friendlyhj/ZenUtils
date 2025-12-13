package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionMapClear extends Expression {
    private final Expression map;

    public ExpressionMapClear(ZenPosition position, Expression map) {
        super(position);
        this.map = map;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        map.compile(true, environment);
        environment.getOutput().invokeInterface("java/util/Map", "clear", "()V");
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }
}
