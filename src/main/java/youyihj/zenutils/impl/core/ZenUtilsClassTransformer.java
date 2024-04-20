package youyihj.zenutils.impl.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.core.asm.ASMParsedExpression;

import java.io.File;
import java.io.IOException;

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
            byte[] byteArray = classWriter.toByteArray();
            try {
                FileUtils.writeByteArrayToFile(new File("ParsedExpression.class"), byteArray);
                return byteArray;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return basicClass;
    }
}
