package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;

import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class ExpressionNativeConstructorCall extends Expression {
    private final ExecutableData constructor;
    private final IEnvironmentGlobal environment;
    private final Expression[] arguments;

    public ExpressionNativeConstructorCall(ZenPosition position, ExecutableData constructor, IEnvironmentGlobal environment, Expression[] arguments) {
        super(position);
        this.constructor = constructor;
        this.environment = environment;
        this.arguments = arguments;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        output.newObject(getType().toASMType().getInternalName());
        output.dup();
        for(int i = 0; i < arguments.length; i++) {
            Expression argument = arguments[i];
            argument.cast(getPosition(), environment, environment.getType(constructor.parameters().get(i).javaType())).compile(true, environment);
        }
        String signatureBuilder = constructor.parameters().stream()
                                        .map(TypeData::descriptor)
                                        .collect(Collectors.joining("", "(", ")V"));
        output.invokeSpecial(constructor.declaringClass().internalName(), "<init>", signatureBuilder);
        if (!result) {
            output.pop();
        }
    }

    @Override
    public ZenType getType() {
        return environment.getType(constructor.declaringClass().javaType());
    }
}
