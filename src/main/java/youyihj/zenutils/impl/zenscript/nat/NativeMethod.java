package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.MethodOutput;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.Modifier;

import static stanhebben.zenscript.type.natives.JavaMethod.*;

/**
 * @author youyihj
 */
public class NativeMethod implements IJavaMethod {
    private final ExecutableData executable;

    private final ZenType returnType;
    private final ZenType[] parameterTypes;
    private final boolean special;

    public NativeMethod(ExecutableData executable, ITypeRegistry types) {
        this(executable, types, false);
    }

    public NativeMethod(ExecutableData executable, ITypeRegistry types, boolean special) {
        this.executable = executable;
        this.special = special;

        this.returnType = types.getType(executable.returnType().javaType());
        this.parameterTypes = executable.parameters().stream()
                .map(TypeData::javaType)
                .map(types::getType)
                .toArray(ZenType[]::new);
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(executable.modifiers());
    }

    @Override
    public boolean accepts(int numArguments) {
        return (executable.parameterCount() == numArguments) || (executable.isVarArgs() && numArguments > executable.parameterCount());
    }

    @Override
    public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
        return getPriority(environment, arguments) > 0;
    }

    @Override
    public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
        // actually copied from JavaMethodGenerated
        int result = PRIORITY_HIGH;
        if(arguments.length > parameterTypes.length) {
            if(isVarargs()) {
                ZenType arrayType = parameterTypes[parameterTypes.length - 1];
                ZenType baseType = ((ZenTypeArray) arrayType).getBaseType();
                for(int i = parameterTypes.length - 1; i < arguments.length; i++) {
                    ZenType argType = arguments[i].getType();
                    if(argType.equals(baseType)) {
                        // OK
                    } else if(argType.canCastImplicit(baseType, environment)) {
                        result = PRIORITY_LOW;
                    } else {
                        return PRIORITY_INVALID;
                    }
                }
            } else {
                return PRIORITY_INVALID;
            }
        } else if(arguments.length < parameterTypes.length) {
            return PRIORITY_INVALID;
        }

        int checkUntil = arguments.length;
        if(arguments.length == parameterTypes.length && isVarargs()) {
            ZenType arrayType = parameterTypes[parameterTypes.length - 1];
            ZenType baseType = ((ZenTypeArray) arrayType).getBaseType();
            ZenType argType = arguments[arguments.length - 1].getType();

            if(argType.equals(arrayType) || argType.equals(baseType)) {
                // OK
            } else if(argType.canCastImplicit(arrayType, environment)) {
                result = PRIORITY_LOW;
            } else if(argType.canCastImplicit(baseType, environment)) {
                result = PRIORITY_LOW;
            } else {
                return PRIORITY_INVALID;
            }
            checkUntil = arguments.length - 1;
        }

        for(int i = 0; i < checkUntil; i++) {
            ZenType argType = arguments[i].getType();
            ZenType paramType = parameterTypes[i];
            if(!argType.equals(paramType)) {
                if(argType.canCastImplicit(paramType, environment)) {
                    result = PRIORITY_LOW;
                } else {
                    return PRIORITY_INVALID;
                }
            }
        }

        return result;
    }

    @Override
    public void invokeVirtual(MethodOutput output) {
        if(isStatic()) {
            throw new UnsupportedOperationException("Method is static");
        } else {
            if(executable.declaringClass().isInterface()) {
                output.invokeInterface(executable.declaringClass().internalName(), executable.name(), executable.descriptor());
            } else {
                if (special) {
                    output.invokeSpecial(executable.declaringClass().internalName(), executable.name(), executable.descriptor());
                } else {
                    output.invokeVirtual(executable.declaringClass().internalName(), executable.name(), executable.descriptor());
                }
            }
        }
    }

    @Override
    public void invokeStatic(MethodOutput output) {
        if(!isStatic()) {
            throw new UnsupportedOperationException("Method is not static");
        } else {
            output.getVisitor().visitMethodInsn(Opcodes.INVOKESTATIC, executable.declaringClass().internalName(), executable.name(), executable.descriptor(), executable.declaringClass().isInterface());
        }
    }

    @Override
    public ZenType[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public ZenType getReturnType() {
        return returnType;
    }

    @Override
    public boolean isVarargs() {
        return executable.isVarArgs();
    }

    @Override
    public String getErrorDescription() {
        return executable.descriptor();
    }
}
