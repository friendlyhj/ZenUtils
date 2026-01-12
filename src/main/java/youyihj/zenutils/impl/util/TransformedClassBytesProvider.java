package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.IClassTransformer;
import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;
import youyihj.zenutils.impl.member.bytecode.ClassExcludedException;

import java.io.IOException;

/**
 * @author youyihj
 */
public class TransformedClassBytesProvider implements ClassBytesProvider {
    private final ClassBytesProvider parent;
    private final IClassTransformer transformer;

    public TransformedClassBytesProvider(ClassBytesProvider parent, IClassTransformer transformer) {
        this.parent = parent;
        this.transformer = transformer;
    }

    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException {
        return transformer.transform(className, className, parent.getClassBytes(className));
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }
}
