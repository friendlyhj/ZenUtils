package youyihj.zenutils.impl.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.core.asm.ASMParsedExpression;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class ZenUtilsClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("stanhebben.zenscript.parser.expression.ParsedExpression".equals(transformedName)) {
            ClassWriter classWriter = new ClassWriter(0);
            ASMParsedExpression asm = new ASMParsedExpression(Opcodes.ASM5, classWriter);
            new ClassReader(basicClass).accept(asm, 0);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
