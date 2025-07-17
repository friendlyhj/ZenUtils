package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigIntArray")
public class ConfigIntArray extends ConfigPrimitive {
    protected final int[] defaultVal;
    protected ConfigIntArray(ConfigGroup parentIn, String nameIn, int[] defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "[I";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, int[] is) {
        pushInt(methodVisitor, is.length);
        methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        for (int i = 0; i < is.length; i ++) {
            methodVisitor.visitInsn(Opcodes.DUP);
            pushInt(methodVisitor, i);
            pushInt(methodVisitor, is[i]);
            methodVisitor.visitInsn(Opcodes.IASTORE);
        }
    }
}
