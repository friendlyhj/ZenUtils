package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigStringArray")
public class ConfigStringArray extends ConfigPrimitive {
    protected final String[] defaultVal;
    protected ConfigStringArray(ConfigGroup parentIn, String nameIn, String[] defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public String[] getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String createDescription() {
        return "[Ljava/lang/String;";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, String[] s) {
        pushInt(methodVisitor, s.length);
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
        for (int i = 0; i < s.length; i ++) {
            methodVisitor.visitInsn(Opcodes.DUP);
            pushInt(methodVisitor, i);
            methodVisitor.visitLdcInsn(s[i]);
            methodVisitor.visitInsn(Opcodes.AASTORE);
        }
    }
}
