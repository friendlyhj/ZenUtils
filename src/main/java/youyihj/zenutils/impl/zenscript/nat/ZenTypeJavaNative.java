package youyihj.zenutils.impl.zenscript.nat;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import youyihj.zenutils.impl.util.InternalUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youyihj
 */
public class ZenTypeJavaNative extends ZenType {
    private final Class<?> clazz;
    private final Map<String, JavaNativeMemberSymbol> symbols = new HashMap<>();

    private static final IJavaMethod OBJECTS_EQUALS = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Objects.class, "equals", Object.class, Object.class);

    public ZenTypeJavaNative(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
        environment.error(position, "no operator available");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
        if (Comparable.class.isAssignableFrom(clazz)) {
            ZenType canCompareType = environment.getType(InternalUtils.getSingleItfGenericVariable(clazz.asSubclass(Comparable.class), Comparable.class));
            if (right.getType().canCastImplicit(canCompareType, environment)) {
                return new ExpressionCompareGeneric(position, new ExpressionCallVirtual(position, environment, JavaMethod.get(environment, clazz, "compareTo", Object.class), left, right.cast(position, environment, canCompareType)), type);
            }
        }
        if (type == CompareType.EQ) {
            return new ExpressionCallStatic(position, environment, OBJECTS_EQUALS, left, right);
        }
        if (type == CompareType.NE) {
            return new ExpressionArithmeticUnary(position, OperatorType.NOT, new ExpressionCallStatic(position, environment, OBJECTS_EQUALS, left, right));
        }
        environment.error(position, "can not compare " + type + " between " + left.getType().getName() + " and " + right.getType().getName());
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        if ("wrapper".equals(name)) {
            Optional<Method> wrapperCaster = CraftTweakerBridge.INSTANCE.getWrapperCaster(clazz);
            if (wrapperCaster.isPresent()) {
                return new ExpressionCallStatic(position, environment, new JavaMethod(wrapperCaster.get(), environment), value.eval(environment));
            }
        }
        return getSymbol(name, environment, false).receiver(value).instance(position);
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return getSymbol(name, environment, true).instance(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        Expression[] actualArguments = receiver == null ? arguments : ArrayUtils.add(arguments, 0, receiver);
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (canAcceptConstructor(constructor, environment, actualArguments)) {
                return new ExpressionNativeConstructorCall(position, constructor, environment, actualArguments);
            }
        }
        environment.error(position, "no such constructor matched");
        return new ExpressionInvalid(position);
    }

    @Override
    public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
        ICastingRule castingRule = super.getCastingRule(type, environment);
        if (castingRule == null) {
            return new CastingRuleCoerced(this, type);
        }
        return castingRule;
    }

    @Override
    public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
        CraftTweakerBridge.INSTANCE.getWrapperCaster(clazz).ifPresent(it ->
                rules.registerCastingRule(environment.getType(it.getReturnType()), new CastingRuleStaticMethod(new JavaMethod(it, environment)))
        );
    }

    @Override
    public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
        return null;
    }

    @Override
    public Class<?> toJavaClass() {
        return clazz;
    }

    @Override
    public Type toASMType() {
        return Type.getType(clazz);
    }

    @Override
    public int getNumberType() {
        return 0;
    }

    @Override
    public String getSignature() {
        return ZenTypeUtil.signature(clazz);
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public String getAnyClassName(IEnvironmentGlobal environment) {
        return getName() + "Any";
    }

    @Override
    public String getName() {
        return "native." + clazz.getCanonicalName();
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }

    private boolean canAcceptConstructor(Constructor<?> constructor, IEnvironmentGlobal environment, Expression[] arguments) {
        Class<?>[] parameters = constructor.getParameterTypes();
        if (arguments.length != parameters.length) {
            return false;
        }

        for (int i = 0; i < arguments.length; i++) {
            if (!arguments[i].getType().canCastImplicit(environment.getType(parameters[i]), environment)) {
                return false;
            }
        }
        return true;
    }

    private JavaNativeMemberSymbol getSymbol(String name, IEnvironmentGlobal environment, boolean isStatic) {
        return symbols.computeIfAbsent(name, it -> JavaNativeMemberSymbol.of(environment, clazz, it, isStatic));
    }
}
