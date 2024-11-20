package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.*;

/**
 * @author youyihj
 */
public class ReflectionTypeData implements TypeData {
    private static final ClassData OBJECT_DATA = ReflectionClassData.of(Object.class);
    private static final ClassData OBJECT_ARRAY_DATA = ReflectionClassData.of(Object[].class);

    private final Type type;
    private final Class<?> clazz;

    /* package-private */ ReflectionTypeData(Type type, Class<?> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    @Override
    public Type javaType() {
        return type;
    }

    @Override
    public String descriptor() {
        return "L" + clazz.getName().replace('.', '/') + ";";
    }

    @Override
    public ClassData asClassData() {
        return ReflectionClassData.of(clazz);
    }
}
