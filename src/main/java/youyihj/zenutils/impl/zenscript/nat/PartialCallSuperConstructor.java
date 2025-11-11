package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ExecutableData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class PartialCallSuperConstructor implements IPartialExpression {
    private final ZenPosition position;
    private final ZenTypeJavaNative type;
    private final List<ExecutableData> constructorCandidates;
    private final List<IJavaMethod> constructorCandidateJavaMethods;

    public PartialCallSuperConstructor(ZenPosition position, ZenTypeJavaNative type, List<ExecutableData> constructorCandidates, ITypeRegistry types) {
        this.position = position;
        this.type = type.toSuper();
        this.constructorCandidates = constructorCandidates;
        this.constructorCandidateJavaMethods = constructorCandidates.stream().map(it -> new NativeMethod(it, types)).collect(Collectors.toList());
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        return new ExpressionSuper(position, type);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        environment.error(position, "not a valid lvalue");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return type.getMember(position, environment, this, name);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        for (ExecutableData constructorCandidate : constructorCandidates) {
            if (ZenTypeJavaNative.canAcceptConstructor(constructorCandidate, environment, values)) {
                return new ExpressionSuperConstructorCall(position, constructorCandidate, environment, values);
            }
        }
        environment.error(position, "no such constructor matched");
        return new ExpressionInvalid(position);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return JavaMethod.predict(constructorCandidateJavaMethods, numArguments);
    }

    @Override
    public IZenSymbol toSymbol() {
        return position1 -> this;
    }

    @Override
    public ZenType getType() {
        return type;
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "can't convert to type");
        return ZenType.ANY;
    }
}
