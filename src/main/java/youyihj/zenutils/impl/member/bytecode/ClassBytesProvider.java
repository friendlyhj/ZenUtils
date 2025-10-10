package youyihj.zenutils.impl.member.bytecode;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author youyihj
 */
public interface ClassBytesProvider extends Closeable {
    byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException;

    @Override
    default void close() throws IOException {

    }
}
