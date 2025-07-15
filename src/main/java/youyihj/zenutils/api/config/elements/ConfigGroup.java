package youyihj.zenutils.api.config.elements;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.*;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.config.ConfigUtils;

import java.util.*;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigGroup")
public class ConfigGroup extends ConfigElement {
    protected List<ConfigElement> children;

    public ConfigGroup(ConfigGroup parentIn, String nameIn) {
        super(parentIn, nameIn);
        this.children = new ArrayList<>();
    }

    private <T extends ConfigElement> T addChild(T element){
        this.children.add(element);
        return element;
    }

    @ZenMethod
    public ConfigGroup category(String nameIn) {
        return addChild(new ConfigGroup(this, nameIn));
    }

    @ZenMethod
    public ConfigElement integer(String nameIn, int defaultVal) {
        return addChild(new ConfigInt(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement integerArray(String nameIn, int... defaultVal) {
        return addChild(new ConfigIntArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement rangedInteger(String nameIn, int defaultVal, int min, int max) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, min, max));
    }

    @ZenMethod
    public ConfigElement upperRangedInteger(String nameIn, int defaultVal, int max) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, Integer.MIN_VALUE, max));
    }

    @ZenMethod
    public ConfigElement lowerRangedInteger(String nameIn, int defaultVal, int min) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, min, Integer.MAX_VALUE));
    }

    @ZenMethod
    public ConfigElement string(String nameIn, String defaultVal) {
        return addChild(new ConfigString(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement strings(String nameIn, String... strings) {
        return addChild(new ConfigStringArray(this, nameIn, strings));
    }

    @ZenMethod
    public ConfigElement doubleValue(String nameIn, double defaultVal) {
        return addChild(new ConfigDouble(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement doubleValues(String nameIn, double... doubles) {
        return addChild(new ConfigDoubleArray(this, nameIn, doubles));
    }

    @ZenMethod
    public ConfigElement rangedDouble(String nameIn, double defaultVal, double min, double max) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, min, max));
    }

    @ZenMethod
    public ConfigElement upperRangedDouble(String nameIn, double defaultVal, double max) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, Double.MIN_VALUE, max));
    }

    @ZenMethod
    public ConfigElement lowerRangedDouble(String nameIn, double defaultVal, double min) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, min, Double.MAX_VALUE));
    }

    @ZenMethod
    public ConfigElement booleanValue(String nameIn, boolean defaultVal) {
        return addChild(new ConfigBoolean(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement booleanValues(String nameIn,boolean... defaultVal) {
        return addChild(new ConfigBooleanArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigElement booleanArrayMap(String name, @Optional Map<String, Boolean[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Boolean[].class, defaultVal));
    }

    @ZenMethod
    public ConfigElement doubleArrayMap(String name, @Optional  Map<String, Double[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Double[].class, defaultVal));
    }

    @ZenMethod
    public ConfigElement intArrayMap(String name, @Optional Map<String, Integer[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Integer[].class, defaultVal));
    }

    @ZenMethod
    public ConfigElement stringArrayMap(String name, @Optional Map<String, String[]> defaultVal) {
        return addChild(new ConfigMap(this, name, String[].class, defaultVal));
    }

    @ZenMethod
    public ConfigElement booleanMap(String name, @Optional Map<String, Boolean> defaultVal) {
        return addChild(new ConfigMap(this, name, Boolean.class, defaultVal));
    }

    @ZenMethod
    public ConfigElement doubleMap(String name, @Optional Map<String, Double> defaultVal) {
        return addChild(new ConfigMap(this, name, Double.class, defaultVal));
    }

    @ZenMethod
    public ConfigElement intMap(String name, @Optional Map<String, Integer> defaultVal) {
        return addChild(new ConfigMap(this, name, Integer.class, defaultVal));
    }

    @ZenMethod
    public ConfigElement stringMap(String name, @Optional Map<String, String> defaultVal) {
        return addChild(new ConfigMap(this, name, String.class, defaultVal));
    }

    @ZenMethod
    public ConfigElement configEnum(String name, String defaultValue, String[] enums) {
        return addChild(new ConfigEnum(this, name, defaultValue, enums));
    }


    public String getClassName() {
        String className = "dynamic.zenutils.config." + this.getAbsoluteName();
        {
            int idx = className.lastIndexOf('.') + 1;
            if (idx > 0 && idx < className.length()) {
                className = className.substring(0, idx) +
                        Character.toUpperCase(className.charAt(idx)) +
                        className.substring(idx + 1);
            }
        }
        return className;
    }

    public Set<String> getClasses() {
        HashSet<String> hashSet = new HashSet<>();
        for (ConfigElement element : this.children) {
            if (element instanceof ConfigGroup) {
                hashSet.addAll(((ConfigGroup)element).getClasses());
            }
        }
        hashSet.add(this.getClassName());
        return hashSet;
    }

    @ZenMethod
    public void register() {
        register0();
        if (this.parent == null) {
            try {
                for (String clsN : this.getClasses()) {
                    CraftTweakerAPI.registerClass(Class.forName(clsN));
                }
                ConfigUtils.ConfigAnytimeAnytime.register(Class.forName(getClassName()));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    protected void register0() {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor = null;
        MethodVisitor methodVisitor = null;
        AnnotationVisitor annotationVisitor0 = null;
        String className = getClassName().replace('.', '/');
        boolean isInstance = this.getParent() != null;

        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, null, "java/lang/Object", null);
        classWriter.visitSource(".zen_script_dynamic", null);

        {
            annotationVisitor0 = classWriter.visitAnnotation("Lstanhebben/zenscript/annotations/ZenClass;", true);
            annotationVisitor0.visit("value", className.replace('/', '.'));
            annotationVisitor0.visitEnd();
        }
        {
            if (!isInstance) {
                annotationVisitor0 = classWriter.visitAnnotation("Lnet/minecraftforge/common/config/Config;", true);
                annotationVisitor0.visit("modid", this.getName());
                if (this.getDisplayName() != null) {
                    annotationVisitor0.visit("name", this.getDisplayName());
                }
                annotationVisitor0.visitEnd();
            }
        }

        methodVisitor = classWriter.visitMethod(isInstance ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                isInstance ? "<init>" : "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        if (isInstance) {
            // super();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        }

        for (ConfigElement configElement : this.children) {
            if (configElement instanceof ConfigPrimitive) {
                ConfigPrimitive configPrimitive = (ConfigPrimitive) configElement;
                fieldVisitor = classWriter.visitField(isInstance ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        configPrimitive.getName(), configPrimitive.createDescription(), configPrimitive.createSignature(), null);
                {
                    if (isInstance) methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    configPrimitive.createToStack(methodVisitor);
                    methodVisitor.visitFieldInsn(isInstance ? Opcodes.PUTFIELD : Opcodes.PUTSTATIC, className, configPrimitive.getName(), configPrimitive.createDescription());
                }
            } else if (configElement instanceof ConfigGroup) {
                ConfigGroup configGroup = (ConfigGroup) configElement;
                configGroup.register0();
                String catClass = configGroup.getClassName().replace('.', '/');
                String desc = 'L' + catClass + ';';
                fieldVisitor = classWriter.visitField(isInstance ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        configGroup.getName(), desc, null, null);

                {
                    // {{configCategory.getName()}} = new {{catClass}}();
                    if (isInstance) methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitTypeInsn(Opcodes.NEW, catClass);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, catClass, "<init>", "()V", false);
                    methodVisitor.visitFieldInsn(isInstance ? Opcodes.PUTFIELD : Opcodes.PUTSTATIC, className, configGroup.getName(), desc);
                }
            }
            if (fieldVisitor != null) {
                if (configElement.requiresWorldRestart) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$RequiresWorldRestart;", true);
                    annotationVisitor0.visitEnd();
                }
                if (configElement.requiresMcRestart) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$RequiresMcRestart;", true);
                    annotationVisitor0.visitEnd();
                }
                if (configElement.slidingOption) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$SlidingOption;", true);
                    annotationVisitor0.visitEnd();
                }
                if (configElement.getDisplayName() != null) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$Name;", true);
                    annotationVisitor0.visit("value", configElement.getDisplayName());
                    annotationVisitor0.visitEnd();
                }
                if (configElement.getComment() != null) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$Comment;", true);
                    AnnotationVisitor arrayVisitor = annotationVisitor0.visitArray("value");
                    for (String value : configElement.getComment()) {
                        arrayVisitor.visit(null, value);
                    }
                    arrayVisitor.visitEnd();
                    annotationVisitor0.visitEnd();
                }
                if (configElement.getLangKey() != null) {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$LangKey;", true);
                    annotationVisitor0.visit("value", configElement.getLangKey());
                    annotationVisitor0.visitEnd();
                }
                if (configElement instanceof ConfigRangedDouble) {
                    ConfigRangedDouble configRangedDouble = (ConfigRangedDouble) configElement;
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$RangeDouble;", true);
                    annotationVisitor0.visit("min", configRangedDouble.getMin());
                    annotationVisitor0.visit("max", configRangedDouble.getMax());
                    annotationVisitor0.visitEnd();
                }
                if (configElement instanceof ConfigRangedInt) {
                    ConfigRangedInt configRangedInt = (ConfigRangedInt) configElement;
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/minecraftforge/common/config/Config$RangeInt;", true);
                    annotationVisitor0.visit("min", configRangedInt.getMin());
                    annotationVisitor0.visit("max", configRangedInt.getMax());
                    annotationVisitor0.visitEnd();
                }
                {
                    annotationVisitor0 = fieldVisitor.visitAnnotation("Lstanhebben/zenscript/annotations/ZenProperty;", true);
                    annotationVisitor0.visitEnd();
                }

                fieldVisitor = null;
            }
        }
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(isInstance ? Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC : Opcodes.ACC_PUBLIC,
                isInstance ? "<clinit>" : "<init>", "()V", null, null);
        methodVisitor.visitCode();
        if (!isInstance) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        }
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();

        ConfigUtils.ClassProvider.classes.put(className.replace('/', '.'), classWriter.toByteArray());
    }

}
