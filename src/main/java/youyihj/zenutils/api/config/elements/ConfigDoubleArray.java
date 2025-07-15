package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigDoubleArray")
public class ConfigDoubleArray extends ConfigPrimitive{
    protected final double[] defaultVal;
    protected ConfigDoubleArray(ConfigGroup parentIn, String nameIn, double[] defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "[D";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, double[] d) {
        pushInt(methodVisitor, d.length);
        methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE);
        for (int i = 0; i < d.length; i ++) {
            methodVisitor.visitInsn(Opcodes.DUP);
            pushInt(methodVisitor, i);
            methodVisitor.visitLdcInsn(d[i]);
            methodVisitor.visitInsn(Opcodes.DASTORE);
        }
    }
}
