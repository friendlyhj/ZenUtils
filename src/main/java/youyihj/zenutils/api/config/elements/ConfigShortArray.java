package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigShortArray")
public class ConfigShortArray extends ConfigPrimitive {
    protected final short[] defaultVal;
    protected ConfigShortArray(ConfigGroup parentIn, String nameIn, short[] defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "[S";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, short[] s) {
        push_int(methodVisitor, s.length);
        methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        for (int i = 0; i < s.length; i ++) {
            methodVisitor.visitInsn(Opcodes.DUP);
            push_int(methodVisitor, i);
            push_int(methodVisitor, s[i]);
            methodVisitor.visitInsn(Opcodes.IASTORE);
        }
    }
}
