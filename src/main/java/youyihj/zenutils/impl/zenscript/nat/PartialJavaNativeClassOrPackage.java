package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialType;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.util.InternalUtils;

/**
 * @author youyihj
 */
public class PartialJavaNativeClassOrPackage implements IPartialExpression {
    private final String prefix;
    private final ZenPosition position;

    public PartialJavaNativeClassOrPackage(ZenPosition position, String prefix) {
        this.position = position;
        this.prefix = prefix;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        name = prefix.isEmpty() ? name : prefix + "." + name;
        try {
            ClassData clazz = InternalUtils.getClassDataFetcher().forName(name);
            if (NativeClassValidate.isValid(clazz)) {
                return new PartialType(position, environment.getType(clazz.javaType()));
            } else {
                environment.error(position, clazz.name() + " is not natively accessible");
                return new ExpressionInvalid(position);
            }
        } catch (ClassNotFoundException e) {
            return new PartialJavaNativeClassOrPackage(position, name);
        }
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        environment.error(position, "no such class or method: " + prefix);
        return new ExpressionInvalid(position);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return new ZenType[numArguments];
    }

    @Override
    public IZenSymbol toSymbol() {
        return null;
    }

    @Override
    public ZenType getType() {
        return null;
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        return ZenType.ANY;
    }
}
