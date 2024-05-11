package youyihj.zenutils.impl.zenscript;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionDeepNull extends Expression {
    public ExpressionDeepNull(ZenPosition position) {
        super(position);
    }

    @Override
    public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
        return type.defaultValue(position);
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(result) {
            environment.getOutput().aConstNull();
        }
    }

    @Override
    public ZenType getType() {
        return ZenTypeDeepNull.INSTANCE;
    }

    @Override
    public void compileIf(Label onElse, IEnvironmentMethod environment) {
        environment.getOutput().goTo(onElse);
    }
}
