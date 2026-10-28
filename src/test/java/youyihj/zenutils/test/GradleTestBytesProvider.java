package youyihj.zenutils.test;

import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author youyihj
 */
public class GradleTestBytesProvider implements ClassBytesProvider {
    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException {
        try {
            return Files.readAllBytes(Paths.get("build/classes/java/test/" + className.replace(".", "/") + ".class"));
        } catch (IOException e) {
            throw new ClassNotFoundException(className, e);
        }
    }

    @Override
    public void close() {

    }
}
