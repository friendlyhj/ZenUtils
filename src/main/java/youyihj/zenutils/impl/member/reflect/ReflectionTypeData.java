package youyihj.zenutils.impl.member.reflect;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.*;

/**
 * @author youyihj
 */
public class ReflectionTypeData implements TypeData {
    private static final ClassData OBJECT_DATA = new ReflectionClassData(Object.class);
    private static final ClassData OBJECT_ARRAY_DATA = new ReflectionClassData(Object[].class);

    private final Type type;

    /* package-private */ ReflectionTypeData(Type type) {
        this.type = type;
    }

    @Override
    public Type javaType() {
        return type;
    }

    @Override
    public String descriptor() {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return ReflectionClassDataFetcher.type(rawType).descriptor();
        }
        if (type instanceof TypeVariable) {
            return ReflectionClassDataFetcher.type(((TypeVariable<?>) type).getBounds()[0]).descriptor();
        }
        if (type instanceof WildcardType) {
            return ReflectionClassDataFetcher.type(((WildcardType) type).getUpperBounds()[0]).descriptor();
        }
        if (type instanceof GenericArrayType) {
            return "[" + ReflectionClassDataFetcher.type(((GenericArrayType) type).getGenericComponentType()).descriptor();
        }
        return type.getTypeName();
    }

    @Override
    public ClassData asClassData() {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return rawType instanceof Class ? new ReflectionClassData(((Class<?>) rawType)) : OBJECT_DATA;
        }
        if (type instanceof TypeVariable) {
            Type bound = ((TypeVariable<?>) type).getBounds()[0];
            return bound instanceof Class ? new ReflectionClassData(((Class<?>) bound)) : OBJECT_DATA;
        }
        if (type instanceof WildcardType) {
            Type bound = ((WildcardType) type).getUpperBounds()[0];
            return bound instanceof Class ? new ReflectionClassData(((Class<?>) bound)) : OBJECT_DATA;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return componentType instanceof Class ? new ReflectionClassData(((Class<?>) componentType)) : OBJECT_ARRAY_DATA;
        }
        return new ReflectionClassData(((Class<?>) type));
    }
}
