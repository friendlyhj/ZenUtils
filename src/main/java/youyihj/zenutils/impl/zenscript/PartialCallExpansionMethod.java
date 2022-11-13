package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.ArrayUtils;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.PartialStaticGenerated;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.util.List;

/**
 * @author youyihj
 */
public class PartialCallExpansionMethod extends PartialStaticGenerated {
    private final Expression instance;


    public PartialCallExpansionMethod(ZenPosition position, String owner, String method, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType, Expression instance) {
        super(position, owner, method, signature, arguments, returnType);
        this.instance = instance;
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        return super.call(position, environment, ArrayUtils.addAll(ArrayUtils.toArray(instance), values));
    }
}
