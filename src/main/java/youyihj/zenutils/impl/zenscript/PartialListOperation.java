package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.ArrayUtils;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArray;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author youyihj
 */
public class PartialListOperation implements IPartialExpression {
    private final ZenPosition position;
    private final List<IJavaMethod> methods;
    private final IPartialExpression receiver;
    private final ZenTypeArray arrayType;
    private final boolean returnSelf;
    private final boolean isStatic;

    public PartialListOperation(ZenPosition position, List<IJavaMethod> methods, IPartialExpression receiver, ZenTypeArray arrayType, boolean returnSelf, boolean isStatic) {
        this.position = position;
        this.methods = methods;
        this.receiver = receiver;
        this.arrayType = arrayType;
        this.returnSelf = returnSelf;
        this.isStatic = isStatic;
    }

    public PartialListOperation(ZenPosition position, IJavaMethod methods, IPartialExpression receiver, ZenTypeArray arrayType, boolean returnSelf, boolean isStatic) {
        this(position, Collections.singletonList(methods), receiver, arrayType, returnSelf, isStatic);
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid expression");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        environment.error(position, "cannot alter this final");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "methods have no members");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        IJavaMethod selected;
        if (isStatic) {
            selected = JavaMethod.select(true, methods, environment, ArrayUtils.addAll(ArrayUtils.toArray(receiver.eval(environment)), values));
        } else {
            selected = JavaMethod.select(false, methods, environment, values);
        }
        if (selected == null) {
            environment.error(position, "arguments do not match any method");
            return new ExpressionInvalid(position);
        }
        // ambiguous 1 argument for varargs check
        if (selected.isVarargs() && values.length + (isStatic ? 1 : 0) == selected.getParameterTypes().length) {
            Expression lastValue = values[values.length - 1];
            ZenType lastValueType = lastValue.getType();
            ZenType baseType = arrayType.getBaseType();
            if (lastValueType instanceof ZenTypeArray && ((ZenTypeArray) lastValueType).getBaseType().canCastImplicit(baseType, environment)) {
                values[values.length - 1] = lastValue.cast(position, environment, new ZenTypeArrayBasic(baseType));
            } else if (lastValueType.canCastImplicit(baseType, environment)){
                values[values.length - 1] = new ExpressionArray(position, new ZenTypeArrayBasic(baseType), lastValue.cast(position, environment, baseType));
            } else {
                environment.error(position, "can not cast " + lastValueType + " to " + arrayType);
                return new ExpressionInvalid(position);
            }
        }
        return new ExpressionListOperation(position, environment, selected, arrayType, receiver.eval(environment), values, returnSelf);

    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        if (isStatic) {
            numArguments++;
        }
        ZenType[] predict = JavaMethod.predict(methods, numArguments);
        for (int i = 0; i < predict.length; i++) {
            ZenType type = predict[i];
            if (type == null) {
                continue;
            }
            Class<?> typeClass = type.toJavaClass();
            if (typeClass == Object.class) {
                predict[i] = arrayType.getBaseType();
            } else if (typeClass == Object[].class) {
                predict[i] = new ZenTypeArrayBasic(arrayType.getBaseType());
            } else if (Collection.class.isAssignableFrom(typeClass)) {
                predict[i] = arrayType;
            }
        }
        if (isStatic) {
            predict = ArrayUtils.remove(predict, 0);
        }
        return predict;
    }

    @Override
    public IZenSymbol toSymbol() {
        return null;
    }

    @Override
    public ZenType getType() {
        return returnSelf ? arrayType : methods.get(0).getReturnType();
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        return ZenType.ANY;
    }
}
