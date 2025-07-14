package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigDouble")
public class ConfigDouble extends ConfigPrimitive {
    protected final double defaultVal;

    protected ConfigDouble(ConfigGroup parentIn, String nameIn, double defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public double getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String createDescription() {
        return "D";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, double d) {
        methodVisitor.visitLdcInsn(d);
    }
}
