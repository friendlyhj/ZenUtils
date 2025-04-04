package youyihj.zenutils.impl.zenscript.nat;

import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialType;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.LookupRequester;
import youyihj.zenutils.impl.util.Either;
import youyihj.zenutils.impl.util.InternalUtils;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class JavaNativeMemberSymbol implements IZenSymbol {
    private final String name;
    private final ClassData owner;
    private final IEnvironmentGlobal environment;
    private final boolean isStatic;
    private final LookupRequester lookupRequester;
    private final List<IJavaMethod> methods;
    private final Either<ExecutableData, FieldData> getter;
    private final Either<ExecutableData, FieldData> setter;
    private final IPartialExpression receiver;
    private final boolean special;

    private JavaNativeMemberSymbol(IEnvironmentGlobal environment, ClassData owner, String name, boolean isStatic, LookupRequester lookupRequester, IPartialExpression receiver, boolean special) {
        this.name = name;
        this.environment = environment;
        this.owner = owner;
        this.isStatic = isStatic;
        this.receiver = receiver;
        this.lookupRequester = lookupRequester;
        this.special = special;
        this.methods = MCPReobfuscation.INSTANCE.reobfMethodOverloads(owner, name, lookupRequester)
                                                .filter(it -> validateModifier(it.modifiers()))
                                                .map(it -> new NativeMethod(it, environment, special))
                                                .collect(Collectors.toList());
        this.getter = Either.<ExecutableData, FieldData>left(MCPReobfuscation.INSTANCE.reobfMethod(owner, "get" + StringUtils.capitalize(name), lookupRequester))
                .validateLeft(this::validateGetter)
                .orElseLeft(() -> MCPReobfuscation.INSTANCE.reobfMethod(owner, "is" + StringUtils.capitalize(name), lookupRequester))
                .validateLeft(this::validateGetter)
                .orElseRight(() -> MCPReobfuscation.INSTANCE.reobfField(owner, name, lookupRequester))
                .validateRight(this::validateFieldGet);
        this.setter = Either.<ExecutableData, FieldData>left(MCPReobfuscation.INSTANCE.reobfMethod(owner, "set" + StringUtils.capitalize(name), lookupRequester))
                            .validateLeft(this::validateSetter)
                            .orElseRight(() -> MCPReobfuscation.INSTANCE.reobfField(owner, name, lookupRequester))
                            .validateRight(this::validateFieldSet);
    }

    public static JavaNativeMemberSymbol of(IEnvironmentGlobal environment, ClassData owner, String name, boolean isStatic, LookupRequester lookupRequester) {
        return new JavaNativeMemberSymbol(environment, owner, name, isStatic, lookupRequester, null, false);
    }

    public JavaNativeMemberSymbol receiver(IPartialExpression instanceValue) {
        if (!isStatic) {
            return new JavaNativeMemberSymbol(environment, owner, name, false, lookupRequester, instanceValue, special);
        } else if (instanceValue == null) {
            return this;
        } else {
            throw new IllegalStateException("set instance to static symbol");
        }
    }

    public JavaNativeMemberSymbol special() {
        return new JavaNativeMemberSymbol(environment, owner, name, isStatic, lookupRequester, receiver, true);
    }

    @Override
    public IPartialExpression instance(ZenPosition position) {
        if (!isStatic && receiver == null) {
            throw new IllegalStateException("missing instance value of virtual symbol");
        }
        if (getter.isEmpty() && setter.isEmpty() && methods.isEmpty()) {
            if (isStatic) {
                try {
                    return new PartialType(position, environment.getType(InternalUtils.getClassDataFetcher().forName(owner.name() + "$" + name).javaType()));
                } catch (ClassNotFoundException e) {
                    environment.error(position, "no such static member or nested class: " + owner.name() + "." + name);
                }
            }
        } else {
            return new PartialJavaNativeMember(position, name, methods, receiver, environment, owner, getter, setter, special);
        }
        return new ExpressionInvalid(position);
    }


    private boolean validateGetter(ExecutableData method) {
        return method.parameterCount() == 0 && validateModifier(method.modifiers());
    }

    private boolean validateSetter(ExecutableData method) {
        return method.parameterCount() == 1 && validateModifier(method.modifiers());
    }

    private boolean validateFieldGet(FieldData field) {
        return validateModifier(field.modifiers());
    }

    private boolean validateFieldSet(FieldData field) {
        return validateModifier(field.modifiers()) && !Modifier.isFinal(field.modifiers());
    }

    private boolean validateModifier(int modifier) {
        return Modifier.isStatic(modifier) == isStatic;
    }
}
