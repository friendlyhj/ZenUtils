package youyihj.zenutils.impl.member.reflect;

import net.minecraft.entity.EntityLiving;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.TypeData;

import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class ReflectionClassDataFetcher implements ClassDataFetcher {
    private final ClassLoader classLoader;

    public ReflectionClassDataFetcher(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static TypeData type(Type type, Class<?> clazz) {
        if (type instanceof Class<?>) {
            EntityLiving e = null;
            return new ReflectionClassData(clazz);
        } else {
            return new ReflectionTypeData(type, clazz);
        }
    }

    @Override
    public ClassData forName(String className) throws ClassNotFoundException {
        return new ReflectionClassData(Class.forName(className, false, classLoader));
    }

    @Override
    public ClassData forClass(Class<?> clazz) {
        return new ReflectionClassData(clazz);
    }
}
