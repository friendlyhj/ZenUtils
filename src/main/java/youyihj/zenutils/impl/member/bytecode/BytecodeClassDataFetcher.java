package youyihj.zenutils.impl.member.bytecode;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.member.reflect.ReflectionClassData;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author youyihj
 */
public class BytecodeClassDataFetcher implements ClassDataFetcher, Closeable {
    private ClassBytesProvider bytesProvider;
    private final Map<String, ClassData> cache = new HashMap<>();
    private final Set<String> absentTries = new HashSet<>();
    private final ClassDataFetcher parent;
    private boolean closed;

    public BytecodeClassDataFetcher(ClassDataFetcher parent, ClassBytesProvider bytesProvider) {
        this.parent = parent;
        this.bytesProvider = bytesProvider;
        cache.put("I", ReflectionClassData.of(int.class));
        cache.put("J", ReflectionClassData.of(long.class));
        cache.put("F", ReflectionClassData.of(float.class));
        cache.put("D", ReflectionClassData.of(double.class));
        cache.put("Z", ReflectionClassData.of(boolean.class));
        cache.put("S", ReflectionClassData.of(short.class));
        cache.put("B", ReflectionClassData.of(byte.class));
        cache.put("C", ReflectionClassData.of(char.class));
        cache.put("V", ReflectionClassData.of(void.class));
    }

    public BytecodeClassDataFetcher(ClassDataFetcher parent, List<Path> classpath) {
        this(parent, new ClasspathBytesProvider(classpath));
    }

    @Override
    public ClassData forName(String className) throws ClassNotFoundException {
        if (closed) {
            return parent.forName(className);
        }
        if (cache.containsKey(className)) {
            return cache.get(className);
        } else {
            if (absentTries.contains(className)) {
                throw new ClassNotFoundException(className);
            }
            try {
                ClassData classData = findClass(className);
                cache.put(className, classData);
                return classData;
            } catch (ClassNotFoundException e) {
                absentTries.add(className);
                throw e;
            }
        }
    }

    @Override
    public ClassData forClass(Class<?> clazz) {
        try {
            return forName(clazz.getName());
        } catch (ClassNotFoundException e) {
            return ReflectionClassData.of(clazz);
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        bytesProvider.close();
        closed = true;
        bytesProvider = null;
        cache.clear();
        absentTries.clear();
    }

    /* package-private */ TypeData type(String descriptor, String genericInfo) {
        if (descriptor.length() == 1) {
            try {
                return forName(descriptor);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return new BytecodeTypeData(descriptor, genericInfo, this);
    }

    private ClassData findClass(String className) throws ClassNotFoundException {
        try {
            return new BytecodeClassData(bytesProvider.getClassBytes(className), this);
        } catch (ClassExcludedException e) {
            throw new ClassNotFoundException(className, e);
        } catch (ClassNotFoundException e) {
            return parent.forName(className);
        }
    }
}
