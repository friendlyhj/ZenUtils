package youyihj.zenutils.impl.zenscript.nat;

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
public class ExpressionNativeFieldSet extends Expression {
    private final Field field;
    private final Expression toSet;
    private final IPartialExpression instanceValue;

    public ExpressionNativeFieldSet(ZenPosition position, Field field, Expression toSet, IPartialExpression instanceValue) {
        super(position);
        this.toSet = toSet;
        this.field = field;
        this.instanceValue = instanceValue;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        Expression toSetCasted = toSet.cast(getPosition(), environment, environment.getType(field.getGenericType()));
        if (result) {
            if (instanceValue == null) {
                toSetCasted.compile(true, environment);
                output.putStaticField(field.getDeclaringClass(), field);
            } else {
                instanceValue.eval(environment).compile(true, environment);
                toSetCasted.compile(true, environment);
                output.putField(field.getDeclaringClass(), field.getName(), field.getType());
            }
        }
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }
}
