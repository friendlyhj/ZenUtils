package youyihj.zenutils.impl.zenscript.ifnull;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

/**
 * @author youyihj
 */
public class ExpressionIfNotNull extends Expression {
    private final Expression value;
    private final SymbolLocal valueLocalStore;
    private final Expression expression;

    public ExpressionIfNotNull(ZenPosition position, Expression value, SymbolLocal valueLocalStore, Expression expression) {
        super(position);
        this.value = value;
        this.valueLocalStore = valueLocalStore;
        this.expression = expression;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        value.compile(true, environment);
        int local = environment.getLocal(valueLocalStore);
        output.dup();
        output.store(value.getType().toASMType(), local);
        Label lblExit = new Label();
        if (result) {
            Label lblIfNull = new Label();
            output.ifNull(lblIfNull);
            expression.cast(getPosition(), environment, getType()).compile(true, environment);
            output.goTo(lblExit);
            output.label(lblIfNull);
            output.aConstNull();
        } else {
            output.ifNull(lblExit);
            expression.compile(false, environment);
        }
        output.label(lblExit);
    }

    @Override
    public ZenType getType() {
        return ZenTypeUtil.checkPrimitive(expression.getType());
    }
}
