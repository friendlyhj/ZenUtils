package youyihj.zenutils.impl.member.reflect;

import com.google.common.collect.Lists;
import youyihj.zenutils.impl.member.*;

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

    private final Map<LookupRequester, List<FieldData>> fieldsCache = new EnumMap<>(LookupRequester.class);
    private final Map<LookupRequester, List<ExecutableData>> methodsCache = new EnumMap<>(LookupRequester.class);
    private final Map<LookupRequester, List<ExecutableData>> constructorsCache = new EnumMap<>(LookupRequester.class);

    private static final Map<Class<?>, ReflectionClassData> POOL = new HashMap<>();

    private ReflectionClassData(Class<?> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    public static ReflectionClassData of(Class<?> clazz) {
        return POOL.computeIfAbsent(clazz, ReflectionClassData::new);
    }

    @Override
    public String name() {
        return clazz.getName();
    }

    @Override
    public String internalName() {
        return org.objectweb.asm.Type.getInternalName(clazz);
    }

    @Override
    public List<FieldData> fields(LookupRequester requester) {
        return fieldsCache.computeIfAbsent(requester, this::fields0);
    }

    private List<FieldData> fields0(LookupRequester requester) {
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
        return methodsCache.computeIfAbsent(requester, this::methods0);
    }

    private List<ExecutableData> methods0(LookupRequester requester) {
        List<ExecutableData> methods = new ArrayList<>();
        Set<String> usedDescriptors = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (requester.allows(method.getModifiers())) {
                ReflectionExecutableData methodData = new ReflectionExecutableData(method);
                methods.add(methodData);
                usedDescriptors.add(methodData.name() + methodData.descriptorWithoutReturnType());
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            for (ExecutableData superMethod : superClass.methods(requester)) {
                if (usedDescriptors.add(superMethod.name() + superMethod.descriptorWithoutReturnType())) {
                    methods.add(superMethod);
                }
            }
        }
        for (Class<?> itf : clazz.getInterfaces()) {
            for (Method method : itf.getDeclaredMethods()) {
                if (requester.allows(method.getModifiers())) {
                    ReflectionExecutableData interfaceMethodData = new ReflectionExecutableData(method);
                    if (usedDescriptors.add(interfaceMethodData.name() + interfaceMethodData.descriptorWithoutReturnType())) {
                        methods.add(interfaceMethodData);
                    }
                }
            }
        }
        return methods;
    }

    @Override
    public List<ExecutableData> constructors(LookupRequester requester) {
        return constructorsCache.computeIfAbsent(requester, this::constructors0);
    }

    private List<ExecutableData> constructors0(LookupRequester requester) {
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
        if (name().equals(classData.name())) {
            return true;
        }
        List<ClassData> interfaces = classData.interfaces();
        for (ClassData anInterface : interfaces) {
            if (name().equals(anInterface.name())) {
                return true;
            }
        }
        ClassData superClass = classData.superClass();
        if (superClass != null) {
            return isAssignableFrom(superClass);
        }
        return false;
    }

    @Override
    @Nullable
    public ReflectionClassData superClass() {
        return clazz.getSuperclass() != null ? ReflectionClassData.of(clazz.getSuperclass()) : null;
    }

    @Override
    public List<ClassData> interfaces() {
        return Arrays.stream(clazz.getInterfaces())
                .map(ReflectionClassData::of)
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

    @Override
    public ClassDataFetcher fetcher() {
        return new ReflectionClassDataFetcher(clazz.getClassLoader());
    }
}
