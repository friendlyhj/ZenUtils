package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionClassConstant extends Expression {
    private final Type value;

    public ExpressionClassConstant(ZenPosition position, Type value) {
        super(position);
        this.value = value;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        environment.getOutput().constant(value);
    }

    @Override
    public ZenType getType() {
        return ZenTypeClass.INSTANCE;
    }
}
