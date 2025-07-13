package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigEnum")
public class ConfigEnum<T extends Enum<T>> extends ConfigPrimitive{
    protected Class<T> enumType;
    protected T defaultValue;

    protected ConfigEnum(ConfigGroup parentIn, String nameIn, Class<T> enumType, T defaultValue) {
        super(parentIn, nameIn);
        this.defaultValue = defaultValue;
        this.enumType = enumType;
    }

    @Override
    public String createDescription() {
        return org.objectweb.asm.Type.getDescriptor(enumType);
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(Type.getType(this.enumType));
        methodVisitor.visitLdcInsn(this.defaultValue.name());
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
    }

    public static void createToStack0(MethodVisitor methodVisitor, Enum<?> val) {
        methodVisitor.visitLdcInsn(Type.getType(val.getDeclaringClass()));
        methodVisitor.visitLdcInsn(val.name());
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
    }
}
