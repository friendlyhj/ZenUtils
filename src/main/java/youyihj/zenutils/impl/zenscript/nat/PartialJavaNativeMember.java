package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.util.Either;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author youyihj
 */
public class PartialJavaNativeMember implements IPartialExpression {
    private final ZenPosition position;
    private final String name;
    private final List<IJavaMethod> methods;
    private final IPartialExpression instanceValue;
    private final IEnvironmentGlobal environment;
    private final ClassData owner;
    private final Either<ExecutableData, FieldData> getter;
    private final Either<ExecutableData, FieldData> setter;
    private final boolean special;

    public PartialJavaNativeMember(ZenPosition position, String name, List<IJavaMethod> methods, IPartialExpression instanceValue, IEnvironmentGlobal environment, ClassData owner, Either<ExecutableData, FieldData> getter, Either<ExecutableData, FieldData> setter, boolean special) {
        this.position = position;
        this.name = name;
        this.methods = methods;
        this.instanceValue = instanceValue;
        this.environment = environment;
        this.owner = owner;
        this.getter = getter;
        this.setter = setter;
        this.special = special;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        return new ExpressionNativeGetter(position, getter, instanceValue, environment, special);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        return new ExpressionNativeSetter(position, setter, other, instanceValue.eval(environment), special);
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
                return new DelegatedExpressionCallStatic(position, environment, selected, values);
            } else {
                return new ExpressionCallVirtual(position, environment, selected, instanceValue.eval(environment), values);
            }
        }
        return getNestedZenType(environment)
                .map(it -> it.call(position, environment, isStatic() ? null : instanceValue.eval(environment), values))
                .orElseGet(() -> {
                    environment.error(position, "no such method matched");
                    return new ExpressionInvalid(position);
                });
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return JavaMethod.predict(methods, numArguments);
    }

    @Override
    public IZenSymbol toSymbol() {
        return position1 -> this;
    }

    @Override
    public ZenType getType() {
        return eval(environment).getType();
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        return getNestedZenType(environment).orElseGet(() -> {
            environment.error(position, "no such nested class");
            return ZenType.ANY;
        });
    }

    private Optional<ClassData> getNestedClass() {
        try {
            return Optional.of(InternalUtils.getClassDataFetcher().forName(owner.name() + "$" + name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ZenType> getNestedZenType(IEnvironmentGlobal environment) {
        return getNestedClass().map(TypeData::javaType).map(environment::getType);
    }

    private boolean isStatic() {
        return instanceValue == null;
    }
}
