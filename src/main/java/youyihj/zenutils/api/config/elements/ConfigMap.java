package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ZenRegister
@ZenClass("youyihj.zenutils.config.elements.ConfigMap")
public class ConfigMap extends ConfigPrimitive {
    protected Class<?> type;
    protected Map<String, ?> defaultVal;

    public ConfigMap(ConfigGroup parentIn, String nameIn, Class<?> type, Map<String, ?> defaultVal) {
        super(parentIn, nameIn);
        this.type = type;
        this.defaultVal = defaultVal;
    }

    @Override
    public String createDescription() {
        return "Ljava/util/Map;";
    }

    @Override
    public String createSignature() {
        return "Ljava/util/Map<Ljava/lang/String;"+ Type.getDescriptor(this.type) +">;";
    }

    @Override
    public void createToStack(MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);

        BiConsumer<MethodVisitor, ?> consumer = cast(STACK_PUTTER.get(this.type));

        for (Map.Entry<String, ?> entry : this.defaultVal.entrySet()) {
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitLdcInsn(entry.getKey());
            consumer.accept(methodVisitor, cast(entry.getValue()));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            methodVisitor.visitInsn(Opcodes.POP);
        }
    }

    @SuppressWarnings("unchecked")
    private static<T> T cast(Object o) {
        return (T)o;
    }

    static Map<Class<?>, BiConsumer<MethodVisitor, ?>> STACK_PUTTER = new HashMap<>();

    private static<T> void addStackPutter(final Class<T> c, BiConsumer<MethodVisitor, T> methodVisitorTBiConsumer) {
        STACK_PUTTER.put(c, methodVisitorTBiConsumer);
    }

    private static<T> void addArrayStackPutter(final Class<T[]> c, BiConsumer<MethodVisitor, T> methodVisitorTBiConsumer, Consumer<MethodVisitor> newArray, int collector) {
        STACK_PUTTER.put(java.lang.reflect.Array.newInstance(c, 0).getClass(), (methodVisitor, object) -> {
            T[] t = cast(object);
            push_int(methodVisitor, t.length);
            newArray.accept(methodVisitor);
            for (int i = 0; i < t.length; i ++) {
                methodVisitor.visitInsn(Opcodes.DUP);
                push_int(methodVisitor, i);
                methodVisitorTBiConsumer.accept(methodVisitor, t[i]);
                methodVisitor.visitInsn(collector);
            }
        });
    }

    private static<T> void addIntArrayStackPutter(final Class<T[]> c, BiConsumer<MethodVisitor, T> methodVisitorTBiConsumer) {
        addArrayStackPutter(c, methodVisitorTBiConsumer, (m)->m.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT), Opcodes.IASTORE);
    }

    static {
        addStackPutter(Boolean.class, ConfigBoolean::createToStackBoolean);
        addIntArrayStackPutter(Boolean[].class, ConfigBoolean::createToStackBoolean);
        addStackPutter(Byte.class, ConfigByte::createToStack0);
        addIntArrayStackPutter(Byte[].class, ConfigByte::createToStack0);
        addStackPutter(Character.class, ConfigChar::createToStack0);
        addIntArrayStackPutter(Character[].class, ConfigChar::createToStack0);
        addStackPutter(Double.class, ConfigDouble::createToStack0);
        addArrayStackPutter(Double[].class, ConfigDouble::createToStack0, (m)->m.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE) , Opcodes.DASTORE);
        addStackPutter(Enum.class, ConfigEnum::createToStack0);
        addStackPutter(Float.class, ConfigFloat::createToStack0);
        addArrayStackPutter(Float[].class, ConfigFloat::createToStack0, (m)->m.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT) , Opcodes.FASTORE);
        addStackPutter(Integer.class, ConfigInt::createToStack);
        addIntArrayStackPutter(Integer[].class, ConfigInt::createToStack);
        addStackPutter(Short.class, ConfigShort::createToStack0);
        addIntArrayStackPutter(Short[].class, ConfigShort::createToStack0);
        addStackPutter(String.class, ConfigString::createToStack0);
        addArrayStackPutter(String[].class, ConfigString::createToStack0, m-> m.visitTypeInsn(Opcodes.ANEWARRAY, "jaba/lang/String"), Opcodes.AASTORE);
    }
}
