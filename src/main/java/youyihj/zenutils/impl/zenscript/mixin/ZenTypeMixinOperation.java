package youyihj.zenutils.impl.zenscript.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.Arrays;

/**
 * @author youyihj
 */
public class ZenTypeMixinOperation extends ZenType {
    public static final ZenTypeMixinOperation INSTANCE = new ZenTypeMixinOperation();

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
        if ("call".equals(name)) {
            return new Call(position, value);
        } else {
            environment.error(position, "no such member available");
            return new ExpressionInvalid(position);
        }
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "no such member available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        environment.error(position, "cannot call this type");
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
        return Operation.class;
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
        return "mixin.Operation";
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }

    private class Call implements IPartialExpression {
        private final ZenPosition position;
        private final IPartialExpression value;

        public Call(ZenPosition position, IPartialExpression value) {
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
            ExpressionArray array = new ExpressionArray(position, new ZenTypeArrayBasic(ZenTypeJavaNative.OBJECT), values);
            IJavaMethod method = JavaMethod.get(environment, toJavaClass(), "call", Object[].class);
            return new ExpressionCallVirtual(position, environment, method, value.eval(environment), array);
        }

        @Override
        public ZenType[] predictCallTypes(int numArguments) {
            ZenType[] types = new ZenType[numArguments];
            Arrays.fill(types, ZenTypeJavaNative.OBJECT);
            return types;
        }

        @Override
        public IZenSymbol toSymbol() {
            return null;
        }

        @Override
        public ZenType getType() {
            return ZenTypeJavaNative.OBJECT;
        }

        @Override
        public ZenType toType(IEnvironmentGlobal environment) {
            environment.error(position, "not supported");
            return ANY;
        }
    }
}
