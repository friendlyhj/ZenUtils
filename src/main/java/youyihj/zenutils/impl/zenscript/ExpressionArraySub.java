package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.ArrayUtils;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IntRange;

/**
 * @author youyihj
 */
public class ExpressionArraySub extends Expression {
    private final Expression array;
    private final Expression range;

    public ExpressionArraySub(ZenPosition position, Expression array, Expression range) {
        super(position);
        this.array = array;
        this.range = range;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (!result) {
            return;
        }
        MethodOutput output = environment.getOutput();
        ZenTypeArray type = (ZenTypeArray) getType();
        String arraySignature = type.getBaseType().isPointer() ? "[Ljava/lang/Object;" : type.getSignature();
        array.compile(true, environment);
        range.compile(true, environment);
        output.dup();
        output.invokeVirtual(IntRange.class, "getFrom", int.class);
        output.swap();
        output.invokeVirtual(IntRange.class, "getTo", int.class);
        output.invokeStatic(ZenTypeUtil.internal(ArrayUtils.class), "subarray", "(" + arraySignature + "II)" + arraySignature);
        if (type.getBaseType().isPointer()) {
            output.checkCast(type.getSignature());
        }
    }

    @Override
    public ZenType getType() {
        return array.getType();
    }
}
