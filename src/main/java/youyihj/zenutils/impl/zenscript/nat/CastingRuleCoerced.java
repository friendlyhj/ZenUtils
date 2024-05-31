package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.ICastingRule;

/**
 * @author youyihj
 */
public class CastingRuleCoerced implements ICastingRule {
    private final ZenType from;
    private final ZenType to;

    public CastingRuleCoerced(ZenType from, ZenType to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void compile(IEnvironmentMethod method) {
        method.getOutput().checkCast(to.toASMType().getInternalName());
    }

    @Override
    public ZenType getInputType() {
        return from;
    }

    @Override
    public ZenType getResultingType() {
        return to;
    }
}
