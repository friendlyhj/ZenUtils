package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.util.Either;

/**
 * @author youyihj
 */
public class ExpressionNativeGetter extends Expression {
    private final Either<ExecutableData, FieldData> getter;
    private final IPartialExpression instanceValue;
    private final IEnvironmentGlobal environment;
    private final boolean special;

    public ExpressionNativeGetter(ZenPosition position, Either<ExecutableData, FieldData> getter, IPartialExpression instanceValue, IEnvironmentGlobal environment, boolean special) {
        super(position);
        this.getter = getter;
        this.instanceValue = instanceValue;
        this.environment = environment;
        this.special = special;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        if (result) {
            getter.fold(method -> {
                if (instanceValue == null) {
                    output.invokeStatic(method.declaringClass().internalName(), method.name(), method.descriptor());
                } else {
                    instanceValue.eval(environment).compile(true, environment);
                    if (method.declaringClass().isInterface()) {
                        output.invokeInterface(method.declaringClass().internalName(), method.name(), method.descriptor());
                    } else {
                        if (special) {
                            output.invokeSpecial(method.declaringClass().internalName(), method.name(), method.descriptor());
                        } else {
                            output.invokeVirtual(method.declaringClass().internalName(), method.name(), method.descriptor());
                        }
                    }
                }
            }, field -> {
                if (instanceValue == null) {
                    output.getStaticField(field.declaringClass().internalName(), field.name(), field.type().descriptor());
                } else {
                    instanceValue.eval(environment).compile(true, environment);
                    output.getField(field.declaringClass().internalName(), field.name(), field.type().descriptor());
                }
            }, () -> environment.error(getPosition(), "no such getter"));
        }
    }

    @Override
    public ZenType getType() {
        return environment.getType(getter.fold(it -> it.returnType().javaType(), it -> it.type().javaType(), () -> Object.class));
    }
}
