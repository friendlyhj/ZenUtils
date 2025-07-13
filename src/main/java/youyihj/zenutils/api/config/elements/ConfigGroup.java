package youyihj.zenutils.api.config.elements;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import org.objectweb.asm.*;
import rml.loader.core.ASMUtil;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.*;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigGroup")
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
    public ConfigInt integer(String nameIn, int defaultVal) {
        return addChild(new ConfigInt(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigRangedInt rangedInteger(String nameIn, int defaultVal, int min, int max) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, min, max));
    }

    @ZenMethod
    public ConfigRangedInt upperRangedInteger(String nameIn, int defaultVal, int max) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, Integer.MIN_VALUE, max));
    }

    @ZenMethod
    public ConfigRangedInt lowerRangedInteger(String nameIn, int defaultVal, int min) {
        return addChild(new ConfigRangedInt(this, nameIn, defaultVal, min, Integer.MAX_VALUE));
    }

    @ZenMethod
    public ConfigString string(String nameIn, String defaultVal) {
        return addChild(new ConfigString(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigStringArray strings(String nameIn, String... strings) {
        return addChild(new ConfigStringArray(this, nameIn, strings));
    }

    @ZenMethod
    public ConfigDouble doubleValue(String nameIn, double defaultVal) {
        return addChild(new ConfigDouble(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigDoubleArray doubleValues(String nameIn, double... doubles) {
        return addChild(new ConfigDoubleArray(this, nameIn, doubles));
    }

    @ZenMethod
    public ConfigRangedDouble rangedDouble(String nameIn, double defaultVal, double min, double max) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, min, max));
    }

    @ZenMethod
    public ConfigRangedDouble upperRangedDouble(String nameIn, double defaultVal, double max) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, Double.MIN_VALUE, max));
    }

    @ZenMethod
    public ConfigRangedDouble lowerRangedDouble(String nameIn, double defaultVal, double min) {
        return addChild(new ConfigRangedDouble(this, nameIn, defaultVal, min, Double.MAX_VALUE));
    }

    @ZenMethod
    public ConfigBoolean booleanValue(String nameIn, boolean defaultVal) {
        return addChild(new ConfigBoolean(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigBooleanArray booleanValues(String nameIn, boolean... defaultVal) {
        return addChild(new ConfigBooleanArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigChar character(String nameIn, char defaultVal) {
        return addChild(new ConfigChar(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigCharArray characters(String nameIn, char... defaultVal) {
        return addChild(new ConfigCharArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigFloat floatValue(String nameIn, float defaultVal) {
        return addChild(new ConfigFloat(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigFloatArray floatValues(String nameIn, float... defaultVal) {
        return addChild(new ConfigFloatArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigShort shortValue(String nameIn, short defaultVal) {
        return addChild(new ConfigShort(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigShortArray shortValues(String nameIn, short... defaultVal) {
        return addChild(new ConfigShortArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigByte byteValue(String nameIn, byte defaultVal) {
        return addChild(new ConfigByte(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigByteArray byteValues(String nameIn, byte... defaultVal) {
        return addChild(new ConfigByteArray(this, nameIn, defaultVal));
    }

    @ZenMethod
    public ConfigMap booleanArrayMap(String name, Map<String, Boolean[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Boolean[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap byteArrayMap(String name, Map<String, Byte[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Byte[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap charArrayMap(String name, Map<String, Character[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Character[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap doubleArrayMap(String name, Map<String, Double[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Double[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap floatArrayMap(String name, Map<String, Float[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Float[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap shortArrayMap(String name, Map<String, Short[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Short[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap intArrayMap(String name, Map<String, Integer[]> defaultVal) {
        return addChild(new ConfigMap(this, name, Integer[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap stringArrayMap(String name, Map<String, String[]> defaultVal) {
        return addChild(new ConfigMap(this, name, String[].class, defaultVal));
    }

    @ZenMethod
    public ConfigMap booleanMap(String name, Map<String, Boolean> defaultVal) {
        return addChild(new ConfigMap(this, name, Boolean.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap byteMap(String name, Map<String, Byte> defaultVal) {
        return addChild(new ConfigMap(this, name, Byte.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap charMap(String name, Map<String, Character> defaultVal) {
        return addChild(new ConfigMap(this, name, Character.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap doubleMap(String name, Map<String, Double> defaultVal) {
        return addChild(new ConfigMap(this, name, Double.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap floatMap(String name,Map<String, Float> defaultVal) {
        return addChild(new ConfigMap(this, name, Float.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap shortMap(String name, Map<String, Short> defaultVal) {
        return addChild(new ConfigMap(this, name, Short.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap intMap(String name, Map<String, Integer> defaultVal) {
        return addChild(new ConfigMap(this, name, Integer.class, defaultVal));
    }

    @ZenMethod
    public ConfigMap stringMap(String name, Map<String, String> defaultVal) {
        return addChild(new ConfigMap(this, name, String.class, defaultVal));
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
    public String register() {
        register0();
        if (this.parent == null) {
            try {
                for (String clsN : this.getClasses()) {
                    CraftTweakerAPI.registerClass(Class.forName(clsN));
                }
                return ConfigAnytimeAnytime.register(Class.forName(getClassName()));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        } else return "";
    }

    private static class ConfigAnytimeAnytime {
        private static final MethodHandle CONFIGMANAGER$SYNC;

        static {
            try {
                Class.forName("net.minecraftforge.common.config.ConfigManager", true, Launch.classLoader); // Init first
                // Max privilege
                Field lookup$impl_lookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                lookup$impl_lookup.setAccessible(true);
                MethodHandles.Lookup lookup = ((MethodHandles.Lookup) lookup$impl_lookup.get(null)).in(ConfigManager.class);
                CONFIGMANAGER$SYNC = lookup.findStatic(ConfigManager.class, "sync", MethodType.methodType(void.class, Configuration.class, Class.class, String.class, String.class, boolean.class, Object.class));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Register configuration class that is annotated with {@link Config} here for it to be processed immediately with saving and loading supported.
         * Preferably call this method in a static init block at the very end of your configuration class.
         * @param configClass configuration class that is annotated with {@link Config}
         */
        public static String register(Class<?> configClass) {
            if (!configClass.isAnnotationPresent(Config.class)) {
                return "";
            }
            try {
                return $register(configClass);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        private static String $register(Class<?> configClass) throws Throwable {
            Field configManager$mod_config_classes = ConfigManager.class.getDeclaredField("MOD_CONFIG_CLASSES");
            Field configManager$configs = ConfigManager.class.getDeclaredField("CONFIGS");
            configManager$mod_config_classes.setAccessible(true);
            configManager$configs.setAccessible(true);
            Map<String, Set<Class<?>>> MOD_CONFIG_CLASSES = (Map<String, Set<Class<?>>>) configManager$mod_config_classes.get(null);
            Map<String, Configuration> CONFIGS = (Map<String, Configuration>) configManager$configs.get(null);

            Config config = configClass.getAnnotation(Config.class);
            String modId = config.modid();

            Set<Class<?>> modConfigClasses = MOD_CONFIG_CLASSES.computeIfAbsent(modId, k -> Sets.newHashSet());
            modConfigClasses.add(configClass);

            File configDir = new File(Launch.minecraftHome, "config");
            String cfgName = config.name();
            if (Strings.isNullOrEmpty(cfgName)) {
                cfgName = modId;
            }
            File configFile = new File(configDir, cfgName + ".cfg");
            String configFileAbsolute = configFile.getAbsolutePath();
            Configuration cfg = CONFIGS.get(configFileAbsolute);
            if (cfg == null) {
                cfg = new Configuration(configFile);
                cfg.load();
                CONFIGS.put(configFileAbsolute, cfg);
            }

            CONFIGMANAGER$SYNC.invokeExact(cfg, configClass, modId, config.category(), true, (Object) null);

            cfg.save();

            return configFileAbsolute;
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

        ClassProvider.classes.put(className.replace('/', '.'), classWriter.toByteArray());
    }

    public static class ClassProvider implements IClassTransformer {
        public static final Map<String, byte[]> classes = new HashMap<>();

        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass) {
            return classes.getOrDefault(transformedName, basicClass);
        }
    }
}
