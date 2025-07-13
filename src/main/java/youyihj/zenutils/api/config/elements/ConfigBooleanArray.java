package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigBooleanArray")
public class ConfigBooleanArray extends ConfigPrimitive {
    protected boolean[] defaultVal;

    public ConfigBooleanArray(ConfigGroup parentIn, String nameIn, boolean[] defaultVal){
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "[Z";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, boolean[] booleans) {
        push_int(methodVisitor, booleans.length);
        methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        for (int i = 0; i < booleans.length; i ++) {
            methodVisitor.visitInsn(Opcodes.DUP);
            push_int(methodVisitor, i);
            ConfigBoolean.createToStackBoolean(methodVisitor, booleans[i]);
            methodVisitor.visitInsn(Opcodes.IASTORE);
        }
    }

}
