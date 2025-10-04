package youyihj.zenutils.impl.member;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author youyihj
 */
public interface ClassDataFetcher extends Closeable {
    ClassData forName(String className) throws ClassNotFoundException;

    ClassData forClass(Class<?> clazz);

    @Override
    default void close() throws IOException {

    }
}
