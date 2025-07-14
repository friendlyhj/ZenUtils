package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigString")
public class ConfigString extends ConfigPrimitive {
    protected final String defaultVal;
    protected ConfigString(ConfigGroup parentIn, String nameIn, String defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String createDescription() {
        return "Ljava/lang/String;";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, String s) {
        methodVisitor.visitLdcInsn(s);
    }
}
