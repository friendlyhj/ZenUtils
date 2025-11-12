package youyihj.zenutils.impl.zenscript.nat;

/**
 * @author youyihj
 */
public class ParsedZenClassCompile {
    public static Class<?> compile(String name, byte[] bytecode, ClassLoader classLoader) {
        return new ClassLoader(classLoader) {
            private Class<?> find(byte[] array) {
                return defineClass(name, array, 0, array.length);
            }
        }.find(bytecode);
    }
}
