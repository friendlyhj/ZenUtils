package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.*;

/**
 * @author youyihj
 */
public class ReflectionTypeData implements TypeData {
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
        return org.objectweb.asm.Type.getDescriptor(clazz);
    }

    @Override
    public ClassData asClassData() {
        return ReflectionClassData.of(clazz);
    }
}
