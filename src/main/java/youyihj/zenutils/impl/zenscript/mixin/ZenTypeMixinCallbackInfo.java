package youyihj.zenutils.impl.zenscript.mixin;

import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

/**
 * @author youyihj
 */
public class ZenTypeMixinCallbackInfo extends ZenType {
    public static final ZenTypeMixinCallbackInfo INSTANCE = new ZenTypeMixinCallbackInfo();

    @Override
    public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        if (name.equals("cancel")) {
            return new Cancel(position, value);
        } else {
            environment.error(position, "no such member available");
            return new ExpressionInvalid(position);
        }
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        environment.error(position, "operator not supported");
        return new ExpressionInvalid(position);
    }

    @Override
    public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {

    }

    @Override
    public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
        return null;
    }

    @Override
    public Class<?> toJavaClass() {
        return CallbackInfo.class;
    }

    @Override
    public Type toASMType() {
        return Type.getType(toJavaClass());
    }

    @Override
    public int getNumberType() {
        return 0;
    }

    @Override
    public String getSignature() {
        return ZenTypeUtil.signature(toJavaClass());
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public String getAnyClassName(IEnvironmentGlobal environment) {
        return "";
    }

    @Override
    public String getName() {
        return "mixin.CallbackInfo";
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }

    private class Cancel implements IPartialExpression {
        private final ZenPosition position;
        private final IPartialExpression value;

        public Cancel(ZenPosition position, IPartialExpression value) {
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
                environment.error(position, "cancel method accepts 0 argument");
            }
            try {
                return new ExpressionCallVirtual(position, environment, JavaMethod.get(environment, toJavaClass().getMethod("cancel")), value.eval(environment));
            } catch (NoSuchMethodException e) {
                environment.error(position, "can not find cancel method, this is impossible");
            }
            return new ExpressionInvalid(position);
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
            return VOID;
        }

        @Override
        public ZenType toType(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return ANY;
        }
    }
}
