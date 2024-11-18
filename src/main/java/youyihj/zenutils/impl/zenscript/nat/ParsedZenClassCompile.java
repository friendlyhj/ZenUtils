package youyihj.zenutils.impl.zenscript.nat;

/**
 * @author youyihj
 */
public class ParsedZenClassCompile {
    public static Class<?> compile(String name, byte[] bytecode) {
        return new ClassLoader(ParsedZenClassCompile.class.getClassLoader()) {
            private Class<?> find(byte[] array) {
                return defineClass(name, array, 0, array.length);
            }
        }.find(bytecode);
    }
}
