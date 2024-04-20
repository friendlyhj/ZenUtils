package youyihj.zenutils.impl.core.asm;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import youyihj.zenutils.impl.zenscript.TemplateString;

/**
 * @author youyihj
 */
public class ASMParsedExpression extends ClassVisitor {

    public ASMParsedExpression(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("readPrimaryExpression".equals(name)) {
            return new ReadPrimaryExpressionVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private static class ReadPrimaryExpressionVisitor extends MethodVisitor implements Opcodes {
        public ReadPrimaryExpressionVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            Label templateStringCase = new Label();
            super.visitLookupSwitchInsn(dflt, ArrayUtils.add(keys, TemplateString.TOKEN_ID), ArrayUtils.add(labels, templateStringCase));
            super.visitLabel(templateStringCase);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(ALOAD, 1);
            super.visitMethodInsn(INVOKEVIRTUAL, "stanhebben/zenscript/ZenTokener", "next", "()Lstanhebben/zenscript/parser/Token;", false);
            super.visitVarInsn(ALOAD, 0);
            super.visitVarInsn(ALOAD, 2);
            super.visitMethodInsn(INVOKESTATIC, "youyihj/zenutils/impl/zenscript/TemplateString", "getExpression", "(Lstanhebben/zenscript/parser/Token;Lstanhebben/zenscript/util/ZenPosition;Lstanhebben/zenscript/compiler/IEnvironmentGlobal;)Lstanhebben/zenscript/parser/expression/ParsedExpression;", false);
            super.visitInsn(ARETURN);
        }
    }
}
