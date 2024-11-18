package youyihj.zenutils.impl.zenscript.nat;

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
public class ExpressionNativeSetter extends Expression {
    private final Either<ExecutableData, FieldData> setter;
    private final Expression toSet;
    private final IPartialExpression instanceValue;
    private final boolean special;

    public ExpressionNativeSetter(ZenPosition position, Either<ExecutableData, FieldData> setter, Expression toSet, IPartialExpression instanceValue, boolean special) {
        super(position);
        this.setter = setter;
        this.toSet = toSet;
        this.instanceValue = instanceValue;
        this.special = special;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        setter.fold(method -> {
            Expression toSetCasted = toSet.cast(getPosition(), environment, environment.getType(method.parameters().get(0).javaType()));
            if (instanceValue == null) {
                toSetCasted.compile(true, environment);
                output.invokeStatic(method.declaringClass().internalName(), method.name(), method.descriptor());
            } else {
                instanceValue.eval(environment).compile(true, environment);
                toSetCasted.compile(true, environment);
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
            Expression toSetCasted = toSet.cast(getPosition(), environment, environment.getType(field.type().javaType()));
            if (instanceValue == null) {
                toSetCasted.compile(true, environment);
                output.putStaticField(field.declaringClass().internalName(), field.name(), field.type().descriptor());
            } else {
                instanceValue.eval(environment).compile(true, environment);
                toSetCasted.compile(true, environment);
                output.putField(field.declaringClass().internalName(), field.name(), field.type().descriptor());
            }
        }, () -> environment.error(getPosition(), "no such setter"));
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }
}
