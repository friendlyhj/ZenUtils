package youyihj.zenutils.impl.zenscript;

import crafttweaker.zenscript.GlobalRegistry;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

/**
 * @author youyihj
 */
public class ExpressionCallStaticThenCastWithStringArg extends Expression {
    private final IJavaMethod method;
    private final Expression[] arguments;
    private final ZenType returnType;

    public ExpressionCallStaticThenCastWithStringArg(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, Class<?> returnClass, String argument) {
        super(position);
        this.method = method;
        this.returnType = GlobalRegistry.getTypes().getClassType(returnClass);
        this.arguments = JavaMethod.rematch(position, this.method, environment, new ExpressionString(position, argument));
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();

        for(Expression argument : arguments) {
            argument.compile(true, environment);
        }

        method.invokeStatic(output);
        output.checkCast(returnType.toASMType().getInternalName());

        if(!result) {
            output.pop(returnType.isLarge());
        }
    }

    @Override
    public ZenType getType() {
        return returnType;
    }
}
