package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.ArrayUtils;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionListOperation extends Expression {
    private final IJavaMethod method;
    private final Expression list;
    private final ZenTypeArray listType;
    private final Expression[] arguments;
    private final boolean returnSelf;

    public ExpressionListOperation(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, ZenTypeArray listType, Expression list, Expression[] arguments, boolean returnSelf) {
        super(position);
        this.method = method;
        this.list = list;
        this.listType = listType;
        this.returnSelf = returnSelf;
        if (!method.isStatic()) {
            this.arguments = JavaMethod.rematch(position, method, environment, arguments);
        } else {
            this.arguments = ArrayUtils.remove(JavaMethod.rematch(position, method, environment, ArrayUtils.addAll(ArrayUtils.toArray(list), arguments)), 0);
        }
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        list.compile(true, environment);

        if (returnSelf && result) {
            output.dup();
        }

        for (Expression argument : arguments) {
            argument.compile(true, environment);
        }

        if (method.isStatic()) {
            method.invokeStatic(output);
        } else {
            method.invokeVirtual(output);
        }

        if (method.getReturnType() != ZenType.VOID && (!result || returnSelf)) {
            output.pop(method.getReturnType().isLarge());
        } else if (method.getReturnType().toJavaClass() == Object.class) {
            output.checkCast(listType.getBaseType().toJavaClass());
        }
    }

    @Override
    public ZenType getType() {
        return returnSelf ? listType : method.getReturnType();
    }
}
