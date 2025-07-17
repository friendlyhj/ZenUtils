package youyihj.zenutils.api.config.elements;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.data.IDataConverter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.config.ConfigUtils;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.api.util.ReflectionInvoked;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
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

    @ZenRegister
    @ZenClass("mods.zenutils.config.elements.HashDataMap")
    @ReflectionInvoked(asm = true)
    public static class HashDataMap<K extends String, V> extends HashMap<String, V> implements IData {
        protected DataMap dataMapCache = null;
        protected boolean updated = true;
        public DataMap getDataMap() {
            if (updated) {
                dataMapCache = ConfigUtils.dataMap(InternalUtils.cast(this));
                updated = false;
            }
            return dataMapCache;
        }

        public void update() {
            updated = true;
        }

        @Override
        public IData add(IData other) {
            return getDataMap().add(other);
        }

        @Override
        public IData sub(IData other) {
            return getDataMap().sub(other);
        }

        @Override
        public IData mul(IData other) {
            throw new UnsupportedOperationException("Cannot multiply maps");
        }

        @Override
        public IData div(IData other) {
            throw new UnsupportedOperationException("Cannot divide maps");
        }

        @Override
        public IData mod(IData other) {
            throw new UnsupportedOperationException("Cannot perform modulo with maps");
        }

        @Override
        public IData and(IData other) {
            throw new UnsupportedOperationException("Maps do not support bitwise operations");
        }

        @Override
        public IData or(IData other) {
            throw new UnsupportedOperationException("Maps do not support bitwise operations");
        }

        @Override
        public IData xor(IData other) {
            throw new UnsupportedOperationException("Maps do not support bitwise operations");
        }

        @Override
        public IData neg() {
            throw new UnsupportedOperationException("Cannot negate maps.");
        }

        @Override
        public IData not() {
            throw new UnsupportedOperationException("Maps do not support bitwise operations");
        }

        @Override
        public boolean asBool() {
            throw new UnsupportedOperationException("Cannot cast map to bool");
        }

        @Override
        public byte asByte() {
            throw new UnsupportedOperationException("Cannot cast map to byte");
        }

        @Override
        public short asShort() {
            throw new UnsupportedOperationException("Cannot cast map to short");
        }

        @Override
        public int asInt() {
            throw new UnsupportedOperationException("Cannot cast map to int");
        }

        @Override
        public long asLong() {
            throw new UnsupportedOperationException("Cannot cast map to long");
        }

        @Override
        public float asFloat() {
            throw new UnsupportedOperationException("Cannot cast map to float");
        }

        @Override
        public double asDouble() {
            throw new UnsupportedOperationException("Cannot cast map to double");
        }

        @Override
        public String asString() {
            return getDataMap().asString();
        }

        @Override
        public List<IData> asList() {
            return getDataMap().asList();
        }

        @Override
        public Map<String, IData> asMap() {
            return getDataMap().asMap();
        }

        @Override
        public byte[] asByteArray() {
            return getDataMap().asByteArray();
        }

        @Override
        public int[] asIntArray() {
            return getDataMap().asIntArray();
        }

        @Override
        public IData getAt(int i) {
            return getDataMap().getAt(i);
        }

        @Override
        public void setAt(int i, IData value) {
            throw new UnsupportedOperationException("this map is not modifiable");
        }

        @Override
        public IData memberGet(String name) {
            return getDataMap().memberGet(name);
        }

        @Override
        public void memberSet(String name, IData data) {
            throw new UnsupportedOperationException("this map is not modifiable");
        }

        @Override
        public int length() {
            return getDataMap().length();
        }

        @Override
        public boolean contains(IData data) {
            return getDataMap().contains(data);
        }

        @Override
        public int compareTo(IData data) {
            return getDataMap().compareTo(data);
        }

        @Override
        public boolean equals(IData data) {
            return getDataMap().equals(data);
        }

        @Override
        public IData immutable() {
            return getDataMap();
        }

        @Override
        public IData update(IData data) {
            return getDataMap().update(data);
        }

        @Override
        public <T> T convert(IDataConverter<T> converter) {
            return getDataMap().convert(converter);
        }

        @Override
        public V put(String key, V value) {
            update();
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends V> m) {
            update();
            super.putAll(m);
        }
    }
}
