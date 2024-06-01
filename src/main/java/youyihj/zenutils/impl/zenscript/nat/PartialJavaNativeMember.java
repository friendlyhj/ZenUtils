package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class PartialJavaNativeMember implements IPartialExpression {
    private final ZenPosition position;
    private final String name;
    private final Field field;
    private final List<IJavaMethod> methods;
    private final IPartialExpression instanceValue;
    private final IEnvironmentGlobal environment;

    private PartialJavaNativeMember(ZenPosition position, IEnvironmentGlobal environment, Class<?> clazz, String name, IPartialExpression instanceValue) {
        Field field1 = null;
        this.position = position;
        this.name = name;
        this.instanceValue = instanceValue;
        this.environment = environment;
        try {
            Field field = clazz.getField(name);
            if (Modifier.isStatic(field.getModifiers()) == isStatic()) {
                field1 = field;
            }
        } catch (NoSuchFieldException ignored) {}
        methods = Arrays.stream(clazz.getMethods())
                .filter(it -> name.equals(it.getName()))
                .filter(it -> Modifier.isStatic(it.getModifiers()) == isStatic())
                .map(it -> JavaMethod.get(environment, it))
                .collect(Collectors.toList());
        this.field = field1;
    }

    public static PartialJavaNativeMember ofVirtual(ZenPosition position, IEnvironmentGlobal environment, Class<?> clazz, String name, IPartialExpression instanceValue) {
        return new PartialJavaNativeMember(position, environment, clazz, name, instanceValue);
    }

    public static PartialJavaNativeMember ofStatic(ZenPosition position, IEnvironmentGlobal environment, Class<?> clazz, String name) {
        return new PartialJavaNativeMember(position, environment, clazz, name, null);
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        if (field != null) {
            return new ExpressionNativeFieldGet(position, environment, field, instanceValue);
        } else {
            environment.error(position, "no such field: " + name);
            return new ExpressionInvalid(position);
        }
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        if (field != null) {
            if (!Modifier.isFinal(field.getModifiers())) {
                return new ExpressionNativeFieldSet(position, field, other, instanceValue);
            } else {
                environment.error(position, "invalid lvalue");
            }
        } else {
            environment.error(position, "no such field: " + name);
        }
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return getType().getMember(position, environment, this, name);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        IJavaMethod selected = JavaMethod.select(isStatic(), methods, environment, values);
        if (selected != null) {
            if (isStatic()) {
                // TODO: interface static methods should be wrapped with a plain class
                return new ExpressionCallStatic(position, environment, selected, values);
            } else {
                return new ExpressionCallVirtual(position, environment, selected, instanceValue.eval(environment), values);
            }
        }
        environment.error(position, "no such method matched");
        return new ExpressionInvalid(position);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return JavaMethod.predict(methods, numArguments);
    }

    @Override
    public IZenSymbol toSymbol() {
        return null;
    }

    @Override
    public ZenType getType() {
        return environment.getType(field.getType());
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid type");
        return ZenType.ANY;
    }

    private boolean isStatic() {
        return instanceValue == null;
    }
}