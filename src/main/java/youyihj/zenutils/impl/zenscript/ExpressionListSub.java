package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.value.IntRange;

import java.util.List;

/**
 * @author youyihj
 */
public class ExpressionListSub extends Expression {
    private final Expression list;
    private final Expression range;

    public ExpressionListSub(ZenPosition position, Expression list, Expression range) {
        super(position);
        this.list = list;
        this.range = range;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (!result) {
            return;
        }
        MethodOutput output = environment.getOutput();
        list.compile(true, environment);
        range.compile(true, environment);
        output.dup();
        output.invokeVirtual(IntRange.class, "getFrom", int.class);
        output.swap();
        output.invokeVirtual(IntRange.class, "getTo", int.class);
        output.invokeInterface(List.class, "subList", List.class, int.class, int.class);
    }

    @Override
    public ZenType getType() {
        return list.getType();
    }
}
