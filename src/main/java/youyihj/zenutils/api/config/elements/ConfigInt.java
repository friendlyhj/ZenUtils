package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigInt")
public class ConfigInt extends ConfigPrimitive{
    protected final int defaultVal;

    public ConfigInt(ConfigGroup parentIn, String nameIn, int defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    public int getDefaultVal() {
        return defaultVal;
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        push_int(methodVisitor, this.defaultVal);
    }

    public static void createToStack(MethodVisitor methodVisitor, int i) {
        push_int(methodVisitor, i);
    }

    @Override
    public String createDescription() {
        return "I";
    }
}
