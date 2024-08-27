package youyihj.zenutils.impl.zenscript.nat;

import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
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
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.member.bytecode.BytecodeClassData;
import youyihj.zenutils.impl.member.bytecode.BytecodeClassDataFetcher;
import youyihj.zenutils.impl.member.reflect.ReflectionClassData;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author youyihj
 */
public class ZenTypeJavaNative extends ZenType {
    public static final ZenTypeJavaNative OBJECT = new ZenTypeJavaNative(new ReflectionClassData(Object.class), ZenTypeUtil.EMPTY_REGISTRY);

    private final ClassData clazz;
    private final Map<String, JavaNativeMemberSymbol> symbols = new HashMap<>();
    private final List<ZenTypeJavaNative> superClasses;

    private static final IJavaMethod OBJECTS_EQUALS = JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Objects.class, "equals", Object.class, Object.class);

    public ZenTypeJavaNative(ClassData clazz, ITypeRegistry registry) {
        this.clazz = clazz;
        superClasses = Stream.concat(optionalStream(clazz.superClass()), clazz.interfaces().stream())
                .map(it -> registry.getType(it.javaType()))
                .filter(ZenTypeJavaNative.class::isInstance)
                .map(ZenTypeJavaNative.class::cast)
                .collect(Collectors.toList());
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
//        if (Comparable.class.isAssignableFrom(clazz)) {
//            ZenType canCompareType = environment.getType(InternalUtils.getSingleItfGenericVariable(clazz.asSubclass(Comparable.class), Comparable.class));
//            if (right.getType().canCastImplicit(canCompareType, environment)) {
//                return new ExpressionCompareGeneric(position, new ExpressionCallVirtual(position, environment, JavaMethod.get(environment, clazz, "compareTo", Object.class), left, right.cast(position, environment, canCompareType)), type);
//            }
//        }
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
            Optional<ExecutableData> wrapperCaster = CraftTweakerBridge.INSTANCE.getWrapperCaster(clazz);
            if (wrapperCaster.isPresent()) {
                return new ExpressionCallStatic(position, environment, new NativeMethod(wrapperCaster.get(), environment), value.eval(environment));
            }
        }
        IPartialExpression member = getSymbol(name, environment, false).receiver(value).instance(position);
        if (member instanceof ExpressionInvalid) {
            IPartialExpression memberExpansion = memberExpansion(position, environment, value.eval(environment), name);
            return memberExpansion == null ? member : memberExpansion;
        }
        return member;
    }

    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return getSymbol(name, environment, true).instance(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        Expression[] actualArguments = receiver == null ? arguments : ArrayUtils.add(arguments, 0, receiver);
        List<ExecutableData> constructors = clazz.constructors(true);
        for (ExecutableData constructor : constructors) {
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
                rules.registerCastingRule(environment.getType(it.returnType().javaType()), new CastingRuleStaticMethod(new NativeMethod(it, environment)))
        );
    }

    @Override
    public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
        return null;
    }

    @Override
    public Class<?> toJavaClass() {
        if (clazz instanceof ReflectionClassData) {
            return ((Class<?>) clazz.javaType());
        }
        if (clazz instanceof BytecodeClassData) {
            try {
                return ClassInfoClassLoader.INSTANCE.getClassInfo(((BytecodeClassData) clazz));
            } catch (ClassNotFoundException e) {
                return Object.class;
            }
        }
        return Object.class;
    }

    @Override
    public Type toASMType() {
        return Type.getType(clazz.descriptor());
    }

    @Override
    public int getNumberType() {
        return 0;
    }

    @Override
    public String getSignature() {
        return clazz.descriptor();
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
        return "native." + clazz.name();
    }

    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }

    private boolean canAcceptConstructor(ExecutableData constructor, IEnvironmentGlobal environment, Expression[] arguments) {
        List<TypeData> parameters = constructor.parameters();
        if (arguments.length != parameters.size()) {
            return false;
        }

        for (int i = 0; i < arguments.length; i++) {
            if (!arguments[i].getType().canCastImplicit(environment.getType(parameters.get(i).javaType()), environment)) {
                return false;
            }
        }
        return true;
    }

    private JavaNativeMemberSymbol getSymbol(String name, IEnvironmentGlobal environment, boolean isStatic) {
        return symbols.computeIfAbsent(name, it -> JavaNativeMemberSymbol.of(environment, clazz, it, isStatic));
    }

    @Override
    public IPartialExpression memberExpansion(ZenPosition position, IEnvironmentGlobal environment, Expression value, String member) {
        IPartialExpression expression = super.memberExpansion(position, environment, value, member);
        if (expression == null) {
            for (ZenTypeJavaNative superClass : superClasses) {
                expression = superClass.memberExpansion(position, environment, value, member);
                if (expression != null) {
                    return expression;
                }
            }
        }
        return expression;
    }

    private static <T> Stream<T> optionalStream(T obj) {
        return obj == null ? Stream.empty() : Stream.of(obj);
    }

    private static class ClassInfoClassLoader extends ClassLoader {
        private static final ClassInfoClassLoader INSTANCE = new ClassInfoClassLoader();

        private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
        private WeakReference<BytecodeClassDataFetcher> classDataFetcherRef;

        private ClassInfoClassLoader() {}

        Class<?> getClassInfo(BytecodeClassData classData) throws ClassNotFoundException {
            String className = classData.name();
            if (classes.containsKey(className)) {
                return classes.get(className);
            } else {
                classDataFetcherRef = new WeakReference<>(classData.getClassDataFetcher());
                Class<?> clazz = findClass(classData.name());
                classes.put(className, clazz);
                return clazz;
            }
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return findClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            BytecodeClassDataFetcher classDataFetcher = classDataFetcherRef.get();
            if (classDataFetcher == null) {
                return Launch.classLoader.loadClass(name);
            }
            ClassData classData = classDataFetcher.forName(name);
            if (classData instanceof ReflectionClassData) {
                return ((Class<?>) classData.javaType());
            }
            if (classData instanceof BytecodeClassData) {
                byte[] bytecode = ((BytecodeClassData) classData).getBytecode();
                return defineClass(name, bytecode, 0, bytecode.length);
            }
            throw new ClassNotFoundException(name);
        }
    }
}
