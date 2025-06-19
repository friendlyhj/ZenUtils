package youyihj.zenutils.impl.zenscript.ifnull;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialLocal;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ParsedExpressionIfNotNullMember extends ParsedExpression {
    private final ParsedExpression value;
    private final String memberName;

    public ParsedExpressionIfNotNullMember(ZenPosition position, ParsedExpression value, String memberName) {
        super(position);
        this.value = value;
        this.memberName = memberName;
    }

    @Override
    public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
        ZenPosition position = getPosition();
        IPartialExpression cValue = value.compile(environment, null);
        if (!cValue.getType().isPointer()) {
            environment.error(position, "primitive types have no members");
            return new ExpressionInvalid(position, ZenType.ANY);
        }
        // should be unique?
        String localValueName = "ifNotNullLocal_" + System.identityHashCode(this);
        SymbolLocal symbolLocal = new SymbolLocal(cValue.getType(), true);
        environment.putValue(localValueName, symbolLocal, position);
        return new PartialIfNotNullMember(position, cValue, symbolLocal, new PartialLocal(position, symbolLocal).getMember(position, environment, memberName));
    }
}
