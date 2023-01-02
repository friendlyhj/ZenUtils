package youyihj.zenutils.impl.zenscript;

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
import stanhebben.zenscript.util.ZenTypeUtil;
import youyihj.zenutils.api.util.BracketHandlers;

/**
 * @author youyihj
 */
public class ZenTypeBracketHandlers extends ZenType {
    @Override
    public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        environment.error("No such member of BracketHandlers");
        return new ExpressionInvalid(position, ZenType.ANY);
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
        return BracketHandlers.class;
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
        return BracketHandlers.class.getSimpleName() + "Any";
    }

    @Override
    public String getName() {
        return "mods.zenutils.BracketHandlers";
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }
}
