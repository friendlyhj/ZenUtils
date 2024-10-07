package youyihj.zenutils.impl.member;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public interface ClassData extends TypeData, AnnotatedMember {
    String name();

    String internalName();

    List<FieldData> fields(boolean publicOnly);

    List<ExecutableData> methods(boolean publicOnly);

    List<ExecutableData> constructors(boolean publicOnly);

    default List<ExecutableData> methods(String name, boolean publicOnly) {
        return methods(publicOnly).stream().filter(it -> name.equals(it.name())).collect(Collectors.toList());
    }

    boolean isInterface();

    boolean isAssignableFrom(ClassData classData);

    @Nullable
    ClassData superClass();

    List<ClassData> interfaces();
}
