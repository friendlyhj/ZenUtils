package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionMapRemove extends Expression {
    private final Expression map;
    private final Expression key;
    private final ZenTypeAssociative mapType;

    public ExpressionMapRemove(ZenPosition position, Expression map, Expression key, ZenTypeAssociative mapType) {
        super(position);
        this.map = map;
        this.key = key;
        this.mapType = mapType;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        map.compile(true, environment);
        key.compile(true, environment);
        environment.getOutput().invokeInterface("java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
        if (!result) {
            environment.getOutput().pop();
        }
    }

    @Override
    public ZenType getType() {
        return mapType.getValueType();
    }
}
