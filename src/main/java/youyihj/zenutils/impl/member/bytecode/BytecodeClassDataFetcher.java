package youyihj.zenutils.impl.member.bytecode;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.member.reflect.ReflectionClassData;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * @author youyihj
 */
public class BytecodeClassDataFetcher implements ClassDataFetcher, Closeable {
    private final List<FileSystem> jars = new ArrayList<>();
    private final Map<String, ClassData> cache = new HashMap<>();
    private final Set<String> absentTries = new HashSet<>();
    private final ClassDataFetcher parent;
    private boolean closed;

    public BytecodeClassDataFetcher(ClassDataFetcher parent, List<Path> classpath) {
        this.parent = parent;
        cache.put("I", new ReflectionClassData(int.class));
        cache.put("J", new ReflectionClassData(long.class));
        cache.put("F", new ReflectionClassData(float.class));
        cache.put("D", new ReflectionClassData(double.class));
        cache.put("Z", new ReflectionClassData(boolean.class));
        cache.put("S", new ReflectionClassData(short.class));
        cache.put("B", new ReflectionClassData(byte.class));
        cache.put("C", new ReflectionClassData(char.class));
        cache.put("V", new ReflectionClassData(void.class));
        try {
            for (Path path : classpath) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.getFileName().toString().endsWith(".jar")) {
                            jars.add(FileSystems.newFileSystem(file, null));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read a jar of classpath", e);
        }
    }

    @Override
    public ClassData forName(String className) throws ClassNotFoundException {
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
            return new ReflectionClassData(clazz);
        }
    }

    @Override
    public void close() throws IOException {
        closed = true;
        for (FileSystem jar : jars) {
            jar.close();
        }
    }

    /* package-private */ TypeData type(String descriptor, String genericInfo) {
        if (descriptor.startsWith("L") || descriptor.length() == 1) {
            try {
                return forName(descriptor);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return new BytecodeTypeData(descriptor, genericInfo, this);
    }

    private ClassData findClass(String className) throws ClassNotFoundException {
        if (!closed) {
            String[] split = className.split("\\.");
            String first = split[0];
            String[] more = new String[split.length - 1];
            System.arraycopy(split, 1, more, 0, more.length);
            if (more.length > 0) {
                more[more.length - 1] = more[more.length - 1] + ".class";
            } else {
                first += ".class";
            }
            for (FileSystem jar : jars) {
                Path classPath = jar.getPath(first, more);
                if (Files.exists(classPath)) {
                    try {
                        return new BytecodeClassData(Files.readAllBytes(classPath), this);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return parent.forName(className);
    }
}
