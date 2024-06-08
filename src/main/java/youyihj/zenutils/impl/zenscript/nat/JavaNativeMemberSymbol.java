package youyihj.zenutils.impl.zenscript.nat;

import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.util.Either;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class JavaNativeMemberSymbol implements IZenSymbol {
    private final String name;
    private final Class<?> owner;
    private final IEnvironmentGlobal environment;
    private final boolean isStatic;
    private final List<IJavaMethod> methods;
    private final Either<Method, Field> getter;
    private final Either<Method, Field> setter;
    private final IPartialExpression receiver;

    private JavaNativeMemberSymbol(IEnvironmentGlobal environment, Class<?> owner, String name, boolean isStatic, IPartialExpression receiver) {
        this.name = name;
        this.environment = environment;
        this.owner = owner;
        this.isStatic = isStatic;
        this.receiver = receiver;
        this.methods = MCPReobfuscation.INSTANCE.reobfMethodOverloads(owner, name)
                                                .filter(it -> validateModifier(it.getModifiers()))
                                                .map(it -> JavaMethod.get(environment, it))
                                                .collect(Collectors.toList());
        this.getter = Either.<Method, Field>left(MCPReobfuscation.INSTANCE.reobfMethod(owner, "get" + StringUtils.capitalize(name)))
                .validateLeft(this::validateGetter)
                .orElseLeft(() -> MCPReobfuscation.INSTANCE.reobfMethod(owner, "is" + StringUtils.capitalize(name)))
                .validateLeft(this::validateGetter)
                .orElseRight(() -> MCPReobfuscation.INSTANCE.reobfField(owner, name))
                .validateRight(this::validateFieldGet);
        this.setter = Either.<Method, Field>left(MCPReobfuscation.INSTANCE.reobfMethod(owner, "set" + StringUtils.capitalize(name)))
                            .validateLeft(this::validateSetter)
                            .orElseRight(() -> MCPReobfuscation.INSTANCE.reobfField(owner, name))
                            .validateRight(this::validateFieldSet);
    }

    public static JavaNativeMemberSymbol of(IEnvironmentGlobal environment, Class<?> owner, String name, boolean isStatic) {
        return new JavaNativeMemberSymbol(environment, owner, name, isStatic, null);
    }

    public JavaNativeMemberSymbol receiver(IPartialExpression instanceValue) {
        if (!isStatic) {
            return new JavaNativeMemberSymbol(environment, owner, name, false, instanceValue);
        } else {
            throw new IllegalStateException("set instance to static symbol");
        }
    }

    @Override
    public IPartialExpression instance(ZenPosition position) {
        if (!isStatic && receiver == null) {
            throw new IllegalStateException("missing instance value of virtual symbol");
        }
        return new PartialJavaNativeMember(position, name, methods, receiver, environment, owner, getter, setter);
    }


    private boolean validateGetter(Method method) {
        return method.getParameterCount() == 0 && validateModifier(method.getModifiers());
    }

    private boolean validateSetter(Method method) {
        return method.getParameterCount() == 1 && validateModifier(method.getModifiers());
    }

    private boolean validateFieldGet(Field field) {
        return validateModifier(field.getModifiers());
    }

    private boolean validateFieldSet(Field field) {
        return validateModifier(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
    }

    private boolean validateModifier(int modifier) {
        return Modifier.isStatic(modifier) == isStatic;
    }
}
