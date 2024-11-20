package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
public class ReflectionFieldData extends ReflectionAnnotatedMember implements FieldData {
    private final Field field;

    public ReflectionFieldData(Field field) {
        super(field);
        this.field = field;
    }

    @Override
    public ClassData declaringClass() {
        return ReflectionClassData.of(field.getDeclaringClass());
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public int modifiers() {
        return field.getModifiers();
    }

    @Override
    public TypeData type() {
        return ReflectionClassDataFetcher.type(field.getGenericType(), field.getType());
    }
}
