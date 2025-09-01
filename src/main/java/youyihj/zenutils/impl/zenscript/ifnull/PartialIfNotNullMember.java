package youyihj.zenutils.impl.zenscript.ifnull;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

/**
 * @author youyihj
 */
public class PartialIfNotNullMember implements IPartialExpression {
    private final ZenPosition position;
    private final IPartialExpression value;
    private final SymbolLocal valueLocalStore;
    private final IPartialExpression member;

    public PartialIfNotNullMember(ZenPosition position, IPartialExpression value, SymbolLocal valueLocalStore, IPartialExpression member) {
        this.position = position;
        this.value = value;
        this.valueLocalStore = valueLocalStore;
        this.member = member;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        return new ExpressionIfNotNull(position, value.eval(environment), valueLocalStore, member.eval(environment));
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        return new ExpressionIfNotNull(position, value.eval(environment), valueLocalStore, member.assign(position, environment, other));
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return new PartialIfNotNullMember(position, value, valueLocalStore, member.getMember(position, environment, name));
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        return new ExpressionIfNotNull(position, value.eval(environment), valueLocalStore, member.call(position, environment, values));
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return member.predictCallTypes(numArguments);
    }

    @Override
    public IZenSymbol toSymbol() {
        return position1 -> new PartialIfNotNullMember(position1, value, valueLocalStore, member);
    }

    @Override
    public ZenType getType() {
        return ZenTypeUtil.checkPrimitive(member.getType());
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid type");
        return ZenType.ANY;
    }
}
