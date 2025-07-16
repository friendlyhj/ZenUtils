package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigBoolean")
public class ConfigBoolean extends ConfigPrimitive {
    protected boolean defaultVal;

    public ConfigBoolean(ConfigGroup parentIn, String nameIn, boolean defaultVal) {
        super(parentIn, nameIn);
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "Z";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStackBoolean(methodVisitor, this.defaultVal);
    }

    public static void createToStackBoolean(MethodVisitor methodVisitor, boolean defaultVal) {
        methodVisitor.visitInsn(defaultVal ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
    }
}
