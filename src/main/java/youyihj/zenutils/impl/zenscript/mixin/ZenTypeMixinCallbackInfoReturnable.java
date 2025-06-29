package youyihj.zenutils.impl.zenscript.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

/**
 * @author youyihj
 */
public class ZenTypeMixinCallbackInfoReturnable extends ZenTypeMixinCallbackInfo {
    public static final ZenTypeMixinCallbackInfoReturnable INSTANCE = new ZenTypeMixinCallbackInfoReturnable();

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        switch (name) {
            case "setReturnValue":
                return new SetReturnValue(position, value);
            case "getReturnValue":
                return new GetReturnValue(name, ZenTypeJavaNative.OBJECT, position, value);
            case "getReturnValueB":
                return new GetReturnValue(name, BYTE, position, value);
            case "getReturnValueD":
                return new GetReturnValue(name, DOUBLE, position, value);
            case "getReturnValueF":
                return new GetReturnValue(name, FLOAT, position, value);
            case "getReturnValueI":
                return new GetReturnValue(name, INT, position, value);
            case "getReturnValueJ":
                return new GetReturnValue(name, LONG, position, value);
            case "getReturnValueS":
                return new GetReturnValue(name, SHORT, position, value);
            case "getReturnValueZ":
                return new GetReturnValue(name, BOOL, position, value);
        }
        return super.getMember(position, environment, value, name);
    }

    @Override
    public Class<?> toJavaClass() {
        return CallbackInfoReturnable.class;
    }

    @Override
    public String getName() {
        return "mixin.CallbackInfoReturnable";
    }

    private class SetReturnValue implements IPartialExpression {
        private final ZenPosition position;
        private final IPartialExpression value;

        public SetReturnValue(ZenPosition position, IPartialExpression value) {
            this.position = position;
            this.value = value;
        }

        @Override
        public Expression eval(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
            if (values.length != 1) {
                environment.error(position, "setReturnValue method accepts 1 argument");
            }
            Expression arg = values[0];
            ZenType type = arg.getType();
            IJavaMethod method = JavaMethod.get(environment, toJavaClass(), "setReturnValue", Object.class);
            if (type == INT) {
                arg = new ExpressionCallStatic(position, environment, ZenType.INT_VALUEOF, arg);
            } else if (type == LONG) {
                arg = new ExpressionCallStatic(position, environment, ZenType.LONG_VALUEOF, arg);
            } else if (type == FLOAT) {
                arg = new ExpressionCallStatic(position, environment, ZenType.FLOAT_VALUEOF, arg);
            } else if (type == DOUBLE) {
                arg = new ExpressionCallStatic(position, environment, ZenType.DOUBLE_VALUEOF, arg);
            } else if (type == BYTE) {
                arg = new ExpressionCallStatic(position, environment, ZenType.BYTE_VALUEOF, arg);
            } else if (type == SHORT) {
                arg = new ExpressionCallStatic(position, environment, ZenType.SHORT_VALUEOF, arg);
            } else if (type == BOOL) {
                arg = new ExpressionCallStatic(position, environment, ZenType.BOOL_VALUEOF, arg);
            }
            return new ExpressionCallVirtual(position, environment, method, value.eval(environment), arg);
        }

        @Override
        public ZenType[] predictCallTypes(int numArguments) {
            return new ZenType[1];
        }

        @Override
        public IZenSymbol toSymbol() {
            return null;
        }

        @Override
        public ZenType getType() {
            return VOID;
        }

        @Override
        public ZenType toType(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return ANY;
        }
    }

    private class GetReturnValue implements IPartialExpression {
        private final String methodName;
        private final ZenType returnType;
        private final ZenPosition position;
        private final IPartialExpression value;

        public GetReturnValue(String methodName, ZenType returnType, ZenPosition position, IPartialExpression value) {
            this.methodName = methodName;
            this.returnType = returnType;
            this.position = position;
            this.value = value;
        }

        @Override
        public Expression eval(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
            environment.error(position, "not supported");
            return new ExpressionInvalid(position);
        }

        @Override
        public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
            if (values.length != 0) {
                environment.error(position, "getReturnValue method accepts 0 argument");
                return new ExpressionInvalid(position);
            }
            IJavaMethod method = JavaMethod.get(environment, toJavaClass(), methodName);
            return new ExpressionCallVirtual(position, environment, method, value.eval(environment));
        }

        @Override
        public ZenType[] predictCallTypes(int numArguments) {
            return new ZenType[0];
        }

        @Override
        public IZenSymbol toSymbol() {
            return null;
        }

        @Override
        public ZenType getType() {
            return returnType;
        }

        @Override
        public ZenType toType(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return ANY;
        }
    }
}
