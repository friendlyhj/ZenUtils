package youyihj.zenutils.impl.member.bytecode;

/**
 * @author youyihj
 */
public class BundledBytesProvider implements ClassBytesProvider {
    private final ClassBytesProvider first;
    private final ClassBytesProvider second;

    public BundledBytesProvider(ClassBytesProvider first, ClassBytesProvider second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException {
        try {
            return first.getClassBytes(className);
        } catch (ClassNotFoundException e) {
            return second.getClassBytes(className);
        }
    }
}
