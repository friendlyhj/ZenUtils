package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigShort")
public class ConfigShort extends ConfigPrimitive{
    protected final short defaultVal;

    public ConfigShort(ConfigGroup parentIn, String nameIn, short defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public short getDefaultVal() {
        return defaultVal;
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        push_int(methodVisitor, this.defaultVal);
    }

    public static void createToStack0(MethodVisitor methodVisitor, short s) {
        push_int(methodVisitor, s);
    }

    @Override
    public String createDescription() {
        return "S";
    }
}
