package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.*;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.config.ConfigUtils;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigEnum")
public class ConfigEnum extends ConfigPrimitive{
    protected Class<?> enumType;
    protected Enum<?> defaultValue;

    public ConfigEnum(ConfigGroup parentIn, String nameIn, String defaultValue, String[] enums) {
        super(parentIn, nameIn);
        String enumName = parentIn.getAbsoluteName() + ".Enum" + nameIn;
        {
            String enumInternalName = enumName.replace('.', '/');
            ClassWriter classWriter = new ClassWriter(0);
            FieldVisitor fieldVisitor;
            MethodVisitor methodVisitor;
            AnnotationVisitor annotationVisitor0;

            classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_ENUM, enumInternalName, "Ljava/lang/Enum<L" + enumInternalName + ";>;", "java/lang/Enum", null);

            classWriter.visitSource(".dynamic", null);
            {
                annotationVisitor0 = classWriter.visitAnnotation("Lstanhebben/zenscript/annotations/ZenClass;", true);
                annotationVisitor0.visit("value", enumName);
                annotationVisitor0.visitEnd();
            }
            {
                fieldVisitor = classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_ENUM, "$VALUES", "[L" + enumInternalName + ";", null, null);
                fieldVisitor.visitEnd();
            }
            {
                methodVisitor = classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                methodVisitor.visitCode();
                int idx = 0;
                for (String desc : enums) {
                    {
                        fieldVisitor = classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_ENUM, desc, "L" + enumInternalName + ";", null, null);
                        {
                            annotationVisitor0 = fieldVisitor.visitAnnotation("Lstanhebben/zenscript/annotations/ZenProperty;", true);
                            annotationVisitor0.visitEnd();
                        }
                        fieldVisitor.visitEnd();
                    }

                    methodVisitor.visitTypeInsn(Opcodes.NEW, enumInternalName);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitLdcInsn(desc);
                    pushInt(methodVisitor, idx);
                    idx += 1;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, enumInternalName, "<init>", "(Ljava/lang/String;I)V", false);
                    methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, enumInternalName, desc, "L" + enumInternalName + ";");
                }
                pushInt(methodVisitor, idx);
                methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, enumInternalName);
                idx = 0;
                for (String desc : enums) {
                    methodVisitor.visitInsn(Opcodes.DUP);
                    pushInt(methodVisitor, idx);
                    idx += 1;
                    methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, enumInternalName, desc, "L" + enumInternalName + ";");
                    methodVisitor.visitInsn(Opcodes.AASTORE);
                }
                methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, enumInternalName, "$VALUES", "[L" + enumInternalName + ";");
                methodVisitor.visitInsn(Opcodes.RETURN);
                methodVisitor.visitMaxs(4, 0);
                methodVisitor.visitEnd();
            }

            {
                methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "values", "()[L"+enumInternalName+";", null, null);
                {
                    annotationVisitor0 = methodVisitor.visitAnnotation("Lstanhebben/zenscript/annotations/ZenMethod;", true);
                    annotationVisitor0.visitEnd();
                }
                methodVisitor.visitCode();
                methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, enumInternalName, "$VALUES", "[L"+enumInternalName+";");
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "[L"+enumInternalName+";", "clone", "()Ljava/lang/Object;", false);
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "[L"+enumInternalName+";");
                methodVisitor.visitInsn(Opcodes.ARETURN);
                methodVisitor.visitMaxs(1, 0);
                methodVisitor.visitEnd();
            }
            {
                methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "valueOf", "(Ljava/lang/String;)L"+enumInternalName+";", null, null);
                {
                    annotationVisitor0 = methodVisitor.visitAnnotation("Lstanhebben/zenscript/annotations/ZenMethod;", true);
                    annotationVisitor0.visitEnd();
                }
                methodVisitor.visitCode();
                methodVisitor.visitLdcInsn(Type.getObjectType(enumInternalName));
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, enumInternalName);
                methodVisitor.visitInsn(Opcodes.ARETURN);
                methodVisitor.visitMaxs(2, 1);
                methodVisitor.visitEnd();
            }
            {
                methodVisitor = classWriter.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", "()V", null);
                methodVisitor.visitCode();
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                methodVisitor.visitVarInsn(Opcodes.ILOAD, 2);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
                methodVisitor.visitInsn(Opcodes.RETURN);
                methodVisitor.visitMaxs(3, 3);
                methodVisitor.visitEnd();
            }

            classWriter.visitEnd();

            ConfigUtils.ClassProvider.classes.put(enumName, classWriter.toByteArray());
        }
        try {
            this.enumType = Class.forName(enumName);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        this.defaultValue = Enum.valueOf(ConfigUtils.cast(this.enumType), defaultValue);
    }

    protected ConfigEnum(ConfigGroup parentIn, String nameIn, Class<?> enumType, Enum<?> defaultValue) {
        super(parentIn, nameIn);
        this.defaultValue = defaultValue;
        this.enumType = enumType;
    }

    @Override
    public String createDescription() {
        return Type.getDescriptor(enumType);
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        createToStack0(methodVisitor, this.defaultValue);
    }

    public static void createToStack0(MethodVisitor methodVisitor, Enum<?> val) {
        String name = Type.getType(val.getDeclaringClass()).getInternalName();
        methodVisitor.visitLdcInsn(val.name());
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, name, "valueOf", "(Ljava/lang/String;)L" + name + ";", false);
    }
}
