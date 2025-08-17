package youyihj.zenutils.impl.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class FMLModContainerTransformer implements IClassTransformer, Opcodes {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraftforge.fml.common.FMLModContainer")) {
            ClassWriter classWriter = new ClassWriter(0);
            ClassVisitor visitor = new ASMFMLModContainer(ASM5, classWriter);
            new ClassReader(basicClass).accept(visitor, 0);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static class ASMFMLModContainer extends ClassVisitor {

        public ASMFMLModContainer(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("handleModStateEvent")) {
                return new ASMHandleModStateEvent(api, methodVisitor);
            }
            return methodVisitor;
        }
    }

    private static class ASMHandleModStateEvent extends MethodVisitor {

        public ASMHandleModStateEvent(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (name.equals("invoke")) {
                super.visitIntInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraftforge/fml/common/FMLModContainer", "modInstance", "Ljava/lang/Object;");
                super.visitIntInsn(ALOAD, 1);
                super.visitInsn(ICONST_1);
                super.visitMethodInsn(INVOKESTATIC,
                        "youyihj/zenutils/impl/zenscript/entrypoint/CustomScriptEntrypoint",
                        "runScript",
                        "(Ljava/lang/Object;Lnet/minecraftforge/fml/common/event/FMLEvent;Z)V",
                        false
                );
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                super.visitIntInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraftforge/fml/common/FMLModContainer", "modInstance", "Ljava/lang/Object;");
                super.visitIntInsn(ALOAD, 1);
                super.visitInsn(ICONST_0);
                super.visitMethodInsn(INVOKESTATIC,
                        "youyihj/zenutils/impl/zenscript/entrypoint/CustomScriptEntrypoint",
                        "runScript",
                        "(Ljava/lang/Object;Lnet/minecraftforge/fml/common/event/FMLEvent;Z)V",
                        false
                );
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
}
