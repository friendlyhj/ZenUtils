package youyihj.zenutils.impl.member.reflect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class ReflectionExecutableData extends ReflectionAnnotatedMember implements ExecutableData {
    private static final Logger LOGGER = LogManager.getLogger("ReflectionExecutableData");

    private final Executable executable;

    private TypeData returnType;
    private List<TypeData> parameters;

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
        return ReflectionClassData.of(executable.getDeclaringClass());
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
        if (returnType == null) {
            if (executable instanceof Method) {
                Method method = (Method) executable;
                Type type;
                try {
                    type = method.getGenericReturnType();
                } catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
                    LOGGER.warn("Can not get generic return type of method {}", executable);
                    type = method.getReturnType();
                }
                returnType = ReflectionClassDataFetcher.type(type, method.getReturnType());
            } else {
                returnType = declaringClass();
            }
        }
        return returnType;
    }

    @Override
    public List<TypeData> parameters() {
        if (parameters == null) {
            Class<?>[] parameterTypes = executable.getParameterTypes();
            Type[] genericParameterTypes;
            try {
                genericParameterTypes = executable.getGenericParameterTypes();
            } catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
                LOGGER.warn("Can not get generic parameter types of method {}", executable);
                genericParameterTypes = parameterTypes;
            }
            parameters = new ArrayList<>(parameterTypes.length);
            for (int i = 0; i < genericParameterTypes.length; i++) {
                parameters.add(ReflectionClassDataFetcher.type(genericParameterTypes[i], parameterTypes[i]));
            }
        }
        return parameters;
    }

    @Override
    public boolean isVarArgs() {
        return executable.isVarArgs();
    }
}
