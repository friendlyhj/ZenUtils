package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ZenTypeClass extends ZenType {
    public static final ZenTypeClass INSTANCE = new ZenTypeClass();

    private ZenTypeClass() {
    }

    @Override
    public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        environment.error(position, "do not provide members of java.lang.Class for you!");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "do not provide members of java.lang.Class for you!");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        environment.error(position, "do not provide members of java.lang.Class for you!");
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
        return Class.class;
    }

    @Override
    public Type toASMType() {
        return Type.getType(Class.class);
    }

    @Override
    public int getNumberType() {
        return 0;
    }

    @Override
    public String getSignature() {
        return "Ljava/lang/Class;";
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public String getAnyClassName(IEnvironmentGlobal environment) {
        return "ClassAny";
    }

    @Override
    public String getName() {
        return "native.java.lang.Class";
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }
}
