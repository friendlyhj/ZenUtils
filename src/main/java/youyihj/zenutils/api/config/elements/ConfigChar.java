package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigChar")
public class ConfigChar extends ConfigPrimitive {
    protected final char defaultVal;

    public ConfigChar(ConfigGroup parentIn, String nameIn, char defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public char getDefaultVal() {
        return defaultVal;
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        push_int(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, char c) {
        push_int(methodVisitor, c);
    }

    @Override
    public String createDescription() {
        return "C";
    }
}
