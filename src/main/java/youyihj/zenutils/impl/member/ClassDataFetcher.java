package youyihj.zenutils.impl.member;

/**
 * @author youyihj
 */
public interface ClassDataFetcher {
    ClassData forName(String className) throws ClassNotFoundException;

    ClassData forClass(Class<?> clazz);
}
