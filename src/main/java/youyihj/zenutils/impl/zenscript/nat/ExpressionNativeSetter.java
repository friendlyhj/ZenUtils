package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.util.Either;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author youyihj
 */
public class ExpressionNativeSetter extends Expression {
    private final Either<Method, Field> setter;
    private final Expression toSet;
    private final IPartialExpression instanceValue;

    public ExpressionNativeSetter(ZenPosition position, Either<Method, Field> setter, Expression toSet, IPartialExpression instanceValue) {
        super(position);
        this.setter = setter;
        this.toSet = toSet;
        this.instanceValue = instanceValue;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        setter.fold(method -> {
            Expression toSetCasted = toSet.cast(getPosition(), environment, environment.getType(method.getParameterTypes()[0]));
            if (instanceValue == null) {
                toSetCasted.compile(true, environment);
                output.invokeStatic(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
            } else {
                instanceValue.eval(environment).compile(true, environment);
                toSetCasted.compile(true, environment);
                output.invokeVirtual(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
            }
        }, field -> {
            Expression toSetCasted = toSet.cast(getPosition(), environment, environment.getType(field.getGenericType()));
            if (instanceValue == null) {
                toSetCasted.compile(true, environment);
                output.putStaticField(field.getDeclaringClass(), field);
            } else {
                instanceValue.eval(environment).compile(true, environment);
                toSetCasted.compile(true, environment);
                output.putField(field.getDeclaringClass(), field.getName(), field.getType());
            }
        }, () -> environment.error(getPosition(), "no such setter"));
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }
}
