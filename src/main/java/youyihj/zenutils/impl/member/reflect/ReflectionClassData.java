package youyihj.zenutils.impl.member.reflect;

import com.google.common.collect.Lists;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.LookupRequester;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
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
    public List<FieldData> fields(LookupRequester requester) {
        List<Field> fields = Lists.newArrayList(clazz.getDeclaredFields());
        for (Class<?> superclass = clazz.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
        }
        return fields.stream()
                .filter(it -> requester.allows(it.getModifiers()))
                .map(ReflectionFieldData::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExecutableData> methods(LookupRequester requester) {
        List<ExecutableData> methods = new ArrayList<>();
        Set<String> usedDescriptors = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (requester.allows(method.getModifiers())) {
                ReflectionExecutableData methodData = new ReflectionExecutableData(method);
                methods.add(methodData);
                usedDescriptors.add(methodData.name() + methodData.descriptor());
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            for (ExecutableData superMethod : superClass.methods(requester)) {
                if (usedDescriptors.add(superMethod.name() + superMethod.descriptor())) {
                    methods.add(superMethod);
                }
            }
        }
        for (Class<?> itf : clazz.getInterfaces()) {
            for (Method method : itf.getDeclaredMethods()) {
                if (requester.allows(method.getModifiers())) {
                    ReflectionExecutableData interfaceMethodData = new ReflectionExecutableData(method);
                    if (usedDescriptors.add(interfaceMethodData.name() + interfaceMethodData.descriptor())) {
                        methods.add(interfaceMethodData);
                    }
                }
            }
        }
        return methods;
    }

    @Override
    public List<ExecutableData> constructors(LookupRequester requester) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(it -> requester.allows(it.getModifiers()))
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
