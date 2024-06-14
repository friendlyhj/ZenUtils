package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.IZenIterator;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.iterator.IteratorIterable;
import stanhebben.zenscript.type.iterator.IteratorList;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

/**
 * @author youyihj
 */
public class ZenTypeIterable extends ZenTypeJavaNative {

    private static final IJavaMethod ITERABLE_SIZE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Iterables.class, "size", Iterable.class);
    private static final IJavaMethod NEW_ARRAY_LIST_FROM_ITERABLE = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Lists.class, "newArrayList", Iterable.class);

    private final ZenType baseType;

    public ZenTypeIterable(Class<?> clazz, ZenType baseType) {
        super(clazz);
        this.baseType = baseType;
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        if (name.equals("length")) {
            return getMemberLength(position, environment, value);
        }
        return super.getMember(position, environment, value, name);
    }

    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        if (operator == OperatorType.INDEXGET) {
            return indexGet(position, left, right);
        }
        return super.binary(position, environment, left, right, operator);
    }

    private IPartialExpression getMemberLength(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value) {
        return new ExpressionCallStatic(position, environment, ITERABLE_SIZE, value.eval(environment));
    }

    private Expression indexGet(ZenPosition position, Expression array, Expression index) {
        return new Expression(position) {
            @Override
            public void compile(boolean result, IEnvironmentMethod environment) {
                if (result) {
                    array.compile(true, environment);
                    index.compile(true, environment);
                    environment.getOutput().invokeStatic(Iterables.class, "get", Object.class, Iterable.class, int.class);
                    environment.getOutput().checkCast(ZenTypeUtil.checkPrimitive(getType()).toASMType().getInternalName());
                    if (ZenTypeUtil.isPrimitive(getType())) {
                        ZenTypeUtil.checkPrimitive(getType()).getCastingRule(getType(), environment).compile(environment);
                    }
                }
            }

            @Override
            public ZenType getType() {
                return baseType;
            }
        };
    }

    @Override
    public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
        super.constructCastingRules(environment, rules, followCasters);
        rules.registerCastingRule(new ZenTypeArrayList(baseType), new CastingRuleStaticMethod(NEW_ARRAY_LIST_FROM_ITERABLE));
    }

    @Override
    public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
        if (numValues == 1) {
            return new IteratorIterable(methodOutput.getOutput(), baseType);
        }
        if (numValues == 2) {
            return new IteratorList(methodOutput.getOutput(), baseType);
        }
        return null;
    }

}
