package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
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
public class ExpressionNativeGetter extends Expression {
    private final Either<Method, Field> getter;
    private final IPartialExpression instanceValue;
    private final IEnvironmentGlobal environment;

    public ExpressionNativeGetter(ZenPosition position, Either<Method, Field> getter, IPartialExpression instanceValue, IEnvironmentGlobal environment) {
        super(position);
        this.getter = getter;
        this.instanceValue = instanceValue;
        this.environment = environment;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        if (result) {
            getter.fold(method -> {
                if (instanceValue == null) {
                    output.invokeStatic(method.getDeclaringClass(), method.getName(), method.getReturnType());
                } else {
                    instanceValue.eval(environment).compile(true, environment);
                    output.invokeVirtual(method.getDeclaringClass(), method.getName(), method.getReturnType());
                }
            }, field -> {
                if (instanceValue == null) {
                    output.getStaticField(field.getDeclaringClass(), field);
                } else {
                    instanceValue.eval(environment).compile(true, environment);
                    output.getField(field.getDeclaringClass(), field.getName(), field.getType());
                }
            }, () -> environment.error(getPosition(), "no such getter"));
        }
    }

    @Override
    public ZenType getType() {
        return environment.getType(getter.fold(Method::getGenericReturnType, Field::getGenericType, () -> Object.class));
    }
}
