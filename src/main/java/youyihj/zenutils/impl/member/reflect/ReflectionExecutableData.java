package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class ReflectionExecutableData extends ReflectionAnnotatedMember implements ExecutableData {
    private final Executable executable;

    public ReflectionExecutableData(Executable executable) {
        super(executable);
        this.executable = executable;
    }

    @Override
    public String name() {
        return executable instanceof Constructor ? "<init>" : executable.getName();
    }

    @Override
    public ClassData declaringClass() {
        return new ReflectionClassData(executable.getDeclaringClass());
    }

    @Override
    public int parameterCount() {
        return executable.getParameterCount();
    }

    @Override
    public int modifiers() {
        return executable.getModifiers();
    }

    @Override
    public TypeData returnType() {
        return executable instanceof Method ? ReflectionClassDataFetcher.type(((Method) executable).getGenericReturnType()) : declaringClass();
    }

    @Override
    public List<TypeData> parameters() {
        return Arrays.stream(executable.getGenericParameterTypes())
                .map(ReflectionClassDataFetcher::type)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isVarArgs() {
        return executable.isVarArgs();
    }
}
