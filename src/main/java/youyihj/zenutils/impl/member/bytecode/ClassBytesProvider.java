package youyihj.zenutils.impl.member.bytecode;

import java.io.Closeable;

/**
 * @author youyihj
 */
public interface ClassBytesProvider extends Closeable {
    byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException;
}
