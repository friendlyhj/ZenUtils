package youyihj.zenutils.impl.member;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author youyihj
 */
public interface ClassData extends TypeData, AnnotatedMember {
    String name();

    String internalName();

    List<FieldData> fields(boolean publicOnly);

    List<ExecutableData> methods(boolean publicOnly);

    List<ExecutableData> constructors(boolean publicOnly);

    boolean isInterface();

    boolean isAssignableFrom(ClassData classData);

    @Nullable
    ClassData superClass();

    List<ClassData> interfaces();
}
