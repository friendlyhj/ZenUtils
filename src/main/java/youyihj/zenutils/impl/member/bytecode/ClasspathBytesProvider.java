package youyihj.zenutils.impl.member.bytecode;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class ClasspathBytesProvider implements ClassBytesProvider {
    private final List<FileSystem> jars = new ArrayList<>();
    private boolean closed;

    public ClasspathBytesProvider(List<Path> classpath) {
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
    public byte[] getClassBytes(String className) throws ClassNotFoundException {
        if (closed) {
            throw new ClassNotFoundException(className);
        }
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
                    return Files.readAllBytes(classPath);
                } catch (IOException ignored) {
                }
            }
        }
        throw new ClassNotFoundException(className);
    }

    @Override
    public void close() throws IOException {
        closed = true;
        for (FileSystem jar : jars) {
            jar.close();
        }
    }
}
