package youyihj.zenutils.impl.member;

/**
 * @author youyihj
 */
public interface FieldData extends AnnotatedMember {
    ClassData declaringClass();

    String name();

    int modifiers();

    TypeData type();
}
