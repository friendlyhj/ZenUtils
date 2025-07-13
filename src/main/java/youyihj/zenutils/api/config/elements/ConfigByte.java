package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigByte")
public class ConfigByte extends ConfigPrimitive {
    protected final byte defaultVal;

    public ConfigByte(ConfigGroup parentIn, String nameIn, byte defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public byte getDefaultVal() {
        return defaultVal;
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        push_int(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, byte val) {
        push_int(methodVisitor, val);
    }

    @Override
    public String createDescription() {
        return "B";
    }
}
