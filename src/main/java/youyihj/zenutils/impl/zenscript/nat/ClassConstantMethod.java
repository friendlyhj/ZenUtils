package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.MethodOutput;

/**
 * @author youyihj
 */
public class ClassConstantMethod implements IJavaMethod {
    private final Type type;

    public ClassConstantMethod(Type type) {
        this.type = type;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean accepts(int numArguments) {
        return numArguments == 0;
    }

    @Override
    public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
        return accepts(arguments.length);
    }

    @Override
    public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
        return accepts(environment, arguments) ? JavaMethod.PRIORITY_LOW : JavaMethod.PRIORITY_INVALID;
    }

    @Override
    public void invokeVirtual(MethodOutput output) {
        output.constant(type);
    }

    @Override
    public void invokeStatic(MethodOutput output) {
        output.constant(type);
    }

    @Override
    public ZenType[] getParameterTypes() {
        return new ZenType[0];
    }

    @Override
    public ZenType getReturnType() {
        return ZenTypeClass.INSTANCE;
    }

    @Override
    public boolean isVarargs() {
        return false;
    }

    @Override
    public String getErrorDescription() {
        return "INTERNAL METHOD";
    }
}
