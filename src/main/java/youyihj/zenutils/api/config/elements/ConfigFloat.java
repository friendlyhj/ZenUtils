package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigFloat")
public class ConfigFloat extends ConfigPrimitive {
    protected final float defaultVal;

    protected ConfigFloat(ConfigGroup parentIn, String nameIn, float defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public float getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String createDescription() {
        return "F";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, float f) {
        methodVisitor.visitLdcInsn(f);
    }
}
