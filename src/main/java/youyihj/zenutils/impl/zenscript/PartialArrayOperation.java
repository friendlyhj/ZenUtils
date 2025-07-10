package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.ArrayUtils;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.casting.CastingRuleNone;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.nat.CastingRuleCoerced;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.List;

/**
 * @author youyihj
 */
public class PartialArrayOperation implements IPartialExpression {
    private final ZenPosition position;
    private final List<IJavaMethod> methods;
    private final ZenTypeArray arrayType;
    private final IPartialExpression arrayInstance;

    public PartialArrayOperation(ZenPosition position, List<IJavaMethod> methods, ZenTypeArray arrayType, IPartialExpression arrayInstance) {
        this.position = position;
        this.methods = methods;
        this.arrayType = arrayType;
        this.arrayInstance = arrayInstance;
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
        Expression[] args = ArrayUtils.addAll(ArrayUtils.toArray(arrayInstance.eval(environment)), values);
        IJavaMethod selected = JavaMethod.select(true, methods, environment, args);
        if (selected == null) {
            environment.error(position, "arguments do not match any method");
            return new ExpressionInvalid(position);
        }

        // ambiguous 1 argument for varargs check
        if (selected.isVarargs() && values.length + 1 == selected.getParameterTypes().length) {
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

        if (isObjectArray(arrayType)) {
            args[0] = new ExpressionAs(position, args[0], new CastingRuleNone(arrayType, new ZenTypeArrayBasic(ZenTypeJavaNative.OBJECT)));
            ExpressionCallStatic call = new ExpressionCallStatic(position, environment, selected, args);
            Class<?> callReturnType = call.getType().toJavaClass();
            if (callReturnType == Object[].class) {
                return new ExpressionAs(position, call, new CastingRuleCoerced(new ZenTypeArrayBasic(ZenTypeJavaNative.OBJECT), arrayType));
            } else if (callReturnType == Object.class) {
                return new ExpressionAs(position, call, new CastingRuleCoerced(ZenTypeJavaNative.OBJECT, arrayType.getBaseType()));
            } else {
                return call;
            }
        } else {
            return new ExpressionCallStatic(position, environment, selected, args);
        }
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        ZenType[] predict = JavaMethod.predict(methods, numArguments + 1);
        if (isObjectArray(arrayType)) {
            for (int i = 0; i < predict.length; i++) {
                ZenType type = predict[i];
                if (type == null) {
                    continue;
                }
                if (type.toJavaClass() == Object.class) {
                    predict[i] = arrayType.getBaseType();
                } else if (type.toJavaClass() == Object[].class) {
                    predict[i] = arrayType;
                }
            }
        }
        return ArrayUtils.remove(predict, 0);
    }

    @Override
    public IZenSymbol toSymbol() {
        return null;
    }

    @Override
    public ZenType getType() {
        IJavaMethod firstMethod = methods.get(0);
        if (firstMethod instanceof JavaMethod) {
            Class<?> returnClass = firstMethod.getReturnType().toJavaClass();
            if (returnClass == Object[].class) {
                return arrayType;
            }
            if (returnClass == Object.class) {
                return arrayType.getBaseType();
            }
        }
        return firstMethod.getReturnType();
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        return ZenType.ANY;
    }

    public static boolean isObjectArray(ZenType type) {
        return type instanceof ZenTypeArray && ((ZenTypeArray) type).getBaseType().isPointer();
    }
}
