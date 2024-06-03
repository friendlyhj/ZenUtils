package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
public class ExpressionNativeFieldGet extends Expression {
    private final Field field;
    private final IPartialExpression instanceValue;
    private final IEnvironmentGlobal environment;

    public ExpressionNativeFieldGet(ZenPosition position, IEnvironmentGlobal environment, Field field, IPartialExpression instanceValue) {
        super(position);
        this.environment = environment;
        this.field = field;
        this.instanceValue = instanceValue;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        if (result) {
            if (instanceValue == null) {
                output.getStaticField(field.getDeclaringClass(), field);
            } else {
                instanceValue.eval(environment).compile(true, environment);
                output.getField(field.getDeclaringClass(), field.getName(), field.getType());
            }
        }
    }

    @Override
    public ZenType getType() {
        return environment.getType(field.getGenericType());
    }
}
