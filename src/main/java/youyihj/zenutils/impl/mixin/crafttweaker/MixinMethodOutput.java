package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.commons.LocalVariablesSorter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import stanhebben.zenscript.util.MethodOutput;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
@Mixin(value = MethodOutput.class, remap = false)
public abstract class MixinMethodOutput {
    @Shadow
    @Final
    private LocalVariablesSorter visitor;

    @Shadow
    private boolean debug;

    /**
     * @author youyihj
     * @reason Optimize ldc instruction generation.
     */
    @Overwrite
    public void constant(Object value) {
        if (debug)
            System.out.println("ldc " + value);
        if (value instanceof Integer) {
            int i = (Integer) value;
            if (i >= -1 && i <= 5) {
                visitor.visitInsn(i + 3);
            } else if (i == (byte) i) {
                visitor.visitIntInsn(BIPUSH, i);
            } else if (i == (short) i) {
                visitor.visitIntInsn(SIPUSH, i);
            } else {
                visitor.visitLdcInsn(value);
            }
        } else if (value instanceof Long) {
            long l = ((Long) value);
            if (l == 0L) {
                visitor.visitInsn(LCONST_0);
            } else if (l == 1L) {
                visitor.visitInsn(LCONST_1);
            } else {
                visitor.visitLdcInsn(value);
            }
        } else if (value instanceof Float) {
            float f = ((Float) value);
            if (f == 0.0f) {
                visitor.visitInsn(FCONST_0);
            } else if (f == 1.0f) {
                visitor.visitInsn(FCONST_1);
            } else if (f == 2.0f) {
                visitor.visitInsn(FCONST_2);
            } else {
                visitor.visitLdcInsn(value);
            }
        } else if (value instanceof Double) {
            double d = ((Double) value);
            if (d == 0.0) {
                visitor.visitInsn(DCONST_0);
            } else if (d == 1.0) {
                visitor.visitInsn(DCONST_1);
            } else {
                visitor.visitLdcInsn(value);
            }
        } else {
            visitor.visitLdcInsn(value);
        }
    }
}
