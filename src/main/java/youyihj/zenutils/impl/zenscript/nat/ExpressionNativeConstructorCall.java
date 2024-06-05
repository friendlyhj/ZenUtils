package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class ExpressionNativeConstructorCall extends Expression {
    private final Constructor<?> constructor;
    private final IEnvironmentGlobal environment;
    private final Expression[] arguments;

    public ExpressionNativeConstructorCall(ZenPosition position, Constructor<?> constructor, IEnvironmentGlobal environment, Expression[] arguments) {
        super(position);
        this.constructor = constructor;
        this.environment = environment;
        this.arguments = arguments;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        output.newObject(getType().toJavaClass());
        output.dup();
        for(int i = 0; i < arguments.length; i++) {
            Expression argument = arguments[i];
            argument.cast(getPosition(), environment, environment.getType(constructor.getParameterTypes()[i])).compile(true, environment);
        }
        String signatureBuilder = Arrays.stream(constructor.getParameterTypes())
                                        .map(Type::getDescriptor)
                                        .collect(Collectors.joining("", "(", ")V"));
        output.invokeSpecial(Type.getInternalName(constructor.getDeclaringClass()), "<init>", signatureBuilder);
    }

    @Override
    public ZenType getType() {
        return environment.getType(constructor.getDeclaringClass());
    }
}
