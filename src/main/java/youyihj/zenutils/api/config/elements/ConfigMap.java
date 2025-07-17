package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.impl.util.InternalUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@ZenRegister
@ZenClass("mods.zenutils.config.elements.ConfigMap")
public class ConfigMap extends ConfigPrimitive {

    protected Class<?> type;
    protected Map<String, ?> defaultVal;

    public ConfigMap(ConfigGroup parentIn, String nameIn, Class<?> type, Map<String, ?> defaultVal) {
        super(parentIn, nameIn);
        this.type = type;
        this.defaultVal = defaultVal == null ? new HashMap<>() : defaultVal;
    }

    @Override
    public String createDescription() {
        return "Lyouyihj/zenutils/api/config/elements/ConfigMap$HashDataMap;";
    }

    @Override
    public String createSignature() {
        return "Lyouyihj/zenutils/api/config/elements/ConfigMap$HashDataMap<Ljava/lang/String;"+ Type.getDescriptor(this.type) +">;";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, "youyihj/zenutils/api/config/elements/ConfigMap$HashDataMap");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "youyihj/zenutils/api/config/elements/ConfigMap$HashDataMap", "<init>", "()V", false);

        BiConsumer<MethodVisitor, ?> consumer = InternalUtils.cast(STACK_PUTTER.get(this.type));

        for (Map.Entry<String, ?> entry : this.defaultVal.entrySet()) {
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitLdcInsn(entry.getKey());
            consumer.accept(methodVisitor, InternalUtils.cast(entry.getValue()));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            methodVisitor.visitInsn(Opcodes.POP);
        }
    }

    static Map<Class<?>, BiConsumer<MethodVisitor, ?>> STACK_PUTTER = new HashMap<>();

    private static<T> void addStackPutter(final Class<T> c, BiConsumer<MethodVisitor, T> methodVisitorTBiConsumer) {
        STACK_PUTTER.put(c, methodVisitorTBiConsumer);
        STACK_PUTTER.put(java.lang.reflect.Array.newInstance(c, 0).getClass(), (methodVisitor, object) -> {
            T[] t = InternalUtils.cast(object);
            pushInt(methodVisitor, t.length);
            methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, c.getName().replace('.', '/'));
            for (int i = 0; i < t.length; i ++) {
                methodVisitor.visitInsn(Opcodes.DUP);
                pushInt(methodVisitor, i);
                methodVisitorTBiConsumer.accept(methodVisitor, t[i]);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            }
        });
    }


    static {
        addStackPutter(Boolean.class, (methodVisitor, aBoolean) -> {
            if (aBoolean == null) methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            else if (aBoolean == Boolean.TRUE) {
                methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
            } else methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
        });
        addStackPutter(Double.class, (methodVisitor, aByte) -> {
            methodVisitor.visitLdcInsn(aByte);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        });
        addStackPutter(Enum.class, ConfigEnum::createToStack0);
        addStackPutter(Integer.class, (methodVisitor, aByte) -> {
            pushInt(methodVisitor, aByte);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        });
        addStackPutter(String.class, MethodVisitor::visitLdcInsn);
    }
}
