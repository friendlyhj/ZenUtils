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

    List<FieldData> fields(LookupRequester requester);

    List<ExecutableData> methods(LookupRequester requester);

    List<ExecutableData> constructors(LookupRequester requester);

    default List<ExecutableData> methods(String name, LookupRequester requester) {
        return methods(requester).stream().filter(it -> name.equals(it.name())).collect(Collectors.toList());
    }

    boolean isInterface();

    boolean isAssignableFrom(ClassData classData);

    @Nullable
    ClassData superClass();

    List<ClassData> interfaces();
}
