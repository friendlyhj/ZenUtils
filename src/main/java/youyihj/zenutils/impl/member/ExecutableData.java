package youyihj.zenutils.impl.member;

import java.util.List;

/**
 * @author youyihj
 */
public interface ExecutableData extends AnnotatedMember {
    String name();

    ClassData declaringClass();

    int parameterCount();

    int modifiers();

    TypeData returnType();

    List<TypeData> parameters();

    boolean isVarArgs();

    default String descriptor() {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (TypeData parameter : parameters()) {
            buf.append(parameter.descriptor());
        }
        buf.append(')');
        buf.append(returnType().descriptor());
        return buf.toString();
    }
}
