package youyihj.zenutils.api.util;

import crafttweaker.zenscript.GlobalRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.ZenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
public class BracketHandlers {
    public static final Map<String, Pair<Supplier<?>, ZenType>> CONTENT_FACTORIES = new HashMap<>();
    private static final IEnvironmentGlobal environmentGlobal = GlobalRegistry.makeGlobalEnvironment(new HashMap<>());

    public static Pair<Object, ZenType> get(String content) {
        Pair<Supplier<?>, ZenType> supplierObjectPair = CONTENT_FACTORIES.computeIfAbsent(content, (s) -> {
            try {
                String className = "brackethandlers$" + content.replaceAll("\\W", "_");
                ZenTokener tokener = new ZenTokener(content, GlobalRegistry.getEnvironment(), "", false);
                List<Token> tokens = new ArrayList<>();
                while (tokener.hasNext()) {
                    tokens.add(tokener.next());
                }
                IZenSymbol zenSymbol = GlobalRegistry.resolveBracket(environmentGlobal, tokens);
                ClassWriter classWriter = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
                classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(Supplier.class)});
                MethodVisitor constructor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
                constructor.visitCode();
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                constructor.visitInsn(Opcodes.RETURN);
                constructor.visitMaxs(1, 1);
                constructor.visitEnd();
                MethodOutput methodOutput = new MethodOutput(classWriter, Opcodes.ACC_PUBLIC, "get", "()" + Type.getDescriptor(Object.class), null, null);
                EnvironmentClass environmentClass = new EnvironmentClass(classWriter, environmentGlobal);
                EnvironmentMethod environmentMethod = new EnvironmentMethod(methodOutput, environmentClass);
                methodOutput.start();
                Expression expression = zenSymbol.instance(new ZenPosition(null, 34, 0, "BracketHandlers.java"))
                        .eval(environmentMethod);
                expression.compile(true, environmentMethod);
                methodOutput.returnObject();
                methodOutput.end();
                classWriter.visitEnd();
                InternalClassLoader.INSTANCE.bytecodes.put(className, classWriter.toByteArray());
                ZenType type = expression.getType();
                return Pair.of(((Supplier<?>) Class.forName(className, true, InternalClassLoader.INSTANCE).newInstance()), type);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return Pair.of(supplierObjectPair.getLeft().get(), supplierObjectPair.getRight());
    }

    private static class InternalClassLoader extends ClassLoader {
        private static final InternalClassLoader INSTANCE = new InternalClassLoader(ZenUtils.class.getClassLoader());
        private final Map<String, Class<?>> classes = new HashMap<>();
        private final Map<String, byte[]> bytecodes = new HashMap<>();

        public InternalClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (classes.containsKey(name)) {
                return classes.get(name);
            }
            if (bytecodes.containsKey(name)) {
                byte[] bytes = bytecodes.get(name);
                Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
                classes.put(name, clazz);
                return clazz;
            }
            return super.findClass(name);
        }
    }
}
