package youyihj.zenutils.impl.member.reflect;

import com.google.common.collect.Lists;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class ReflectionClassData extends ReflectionAnnotatedMember implements ClassData {
    private final Class<?> clazz;

    public ReflectionClassData(Class<?> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    public String name() {
        return clazz.getCanonicalName();
    }

    @Override
    public String internalName() {
        return org.objectweb.asm.Type.getInternalName(clazz);
    }

    @Override
    public List<FieldData> fields(boolean publicOnly) {
        if (publicOnly) {
            return Arrays.stream(clazz.getFields())
                    .map(ReflectionFieldData::new)
                    .collect(Collectors.toList());
        } else {
            List<Field> fields = Lists.newArrayList(clazz.getDeclaredFields());
            for (Class<?> superclass = clazz.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
                Arrays.stream(superclass.getDeclaredFields())
                        .filter(it -> Modifier.isProtected(it.getModifiers()))
                        .forEach(fields::add);
            }
            return fields.stream()
                    .map(ReflectionFieldData::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ExecutableData> methods(boolean publicOnly) {
        if (publicOnly) {
            return Arrays.stream(clazz.getMethods())
                    .map(ReflectionExecutableData::new)
                    .collect(Collectors.toList());
        } else {
            List<Method> methods = Lists.newArrayList(clazz.getDeclaredMethods());
            for (Class<?> superclass = clazz.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
                Arrays.stream(superclass.getDeclaredMethods())
                        .filter(it -> Modifier.isProtected(it.getModifiers()))
                        .forEach(methods::add);
            }
            return methods.stream()
                    .map(ReflectionExecutableData::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ExecutableData> constructors(boolean publicOnly) {
        Constructor<?>[] constructors = publicOnly ? clazz.getConstructors() : clazz.getDeclaredConstructors();
        return Arrays.stream(constructors)
                .map(ReflectionExecutableData::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isAssignableFrom(ClassData classData) {
        if (classData instanceof ReflectionClassData) {
            return clazz.isAssignableFrom(((ReflectionClassData) classData).clazz);
        }
        // TODO: check
        return false;
    }

    @Override
    @Nullable
    public ClassData superClass() {
        return clazz.getSuperclass() != null ? new ReflectionClassData(clazz.getSuperclass()) : null;
    }

    @Override
    public List<ClassData> interfaces() {
        return Arrays.stream(clazz.getInterfaces())
                .map(ReflectionClassData::new)
                .collect(Collectors.toList());
    }

    @Override
    public Type javaType() {
        return clazz;
    }

    @Override
    public String descriptor() {
        return org.objectweb.asm.Type.getDescriptor(clazz);
    }

    @Override
    public ClassData asClassData() {
        return this;
    }

    @Override
    public String toString() {
        return descriptor();
    }
}
