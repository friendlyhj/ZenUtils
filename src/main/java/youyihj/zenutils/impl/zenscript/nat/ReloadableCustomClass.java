package youyihj.zenutils.impl.zenscript.nat;

import crafttweaker.mc1120.util.CraftTweakerHacks;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import stanhebben.zenscript.ZenModule;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenTypeUtil;
import youyihj.zenutils.ZenUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
public abstract class ReloadableCustomClass {
    private static final ZenModule CUSTOM_CLASS_MODULE = new ZenModule(new HashMap<>(), ZenUtils.class.getClassLoader());

    private final String name;
    private final Class<?> superClass;
    private final List<Class<?>> interfaces;
    private final List<OuterImplementsInfo> outerImplementsInfos;

    private final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    private boolean compiled = false;

    protected ReloadableCustomClass(String name, Class<?> superClass, List<Class<?>> interfaces, List<OuterImplementsInfo> outerImplementsInfos) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.outerImplementsInfos = outerImplementsInfos;
    }

    public byte[] compile() {
        if (compiled) {
            return cw.toByteArray();
        }
        compiled = true;
        String[] impls = Stream.concat(outerImplementsInfos.stream().map(OuterImplementsInfo::getItf), interfaces.stream())
                                       .map(Type::getInternalName)
                                       .toArray(String[]::new);
        cw.visit(V1_8, ACC_PUBLIC, name, null, Type.getInternalName(superClass), impls);
        for (Constructor<?> constructor : superClass.getConstructors()) {
            compileConstructor(constructor);
        }
        Stream.concat(Stream.of(superClass), interfaces.stream())
              .flatMap(it -> Arrays.stream(it.getMethods()))
              .filter(it -> !Modifier.isFinal(it.getModifiers()))
              .map(it -> new JavaMethod(it, ZenTypeUtil.EMPTY_REGISTRY))
              .forEach(this::compileMethod);
        outerImplementsInfos.forEach(this::compileOuterImplements);
        cw.visitEnd();
        return cw.toByteArray();
    }

    public Class<?> define() {
        ZenModule.classes.put(name, this.compile());
        ClassLoader zsClassLoader = CraftTweakerHacks.getPrivateObject(CUSTOM_CLASS_MODULE, "classLoader");
        try {
            return zsClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(name);
        }
    }

    private void compileConstructor(Constructor<?> constructor) {
        String descriptor = Type.getConstructorDescriptor(constructor);
        MethodVisitor constructorMw = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);
        constructorMw.visitCode();
        constructorMw.visitVarInsn(ALOAD, 0);
        int varIndex = 1;
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            Type parameterASMType = Type.getType(parameterType);
            constructorMw.visitVarInsn(parameterASMType.getOpcode(ILOAD), varIndex);
            varIndex += parameterASMType.getSize();
        }
        constructorMw.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(superClass), "<init>", descriptor, false);
        constructorMw.visitInsn(RETURN);
        constructorMw.visitMaxs(0, 0);
        constructorMw.visitEnd();
    }

    private void compileMethod(JavaMethod method) {
        String functionItfName = ZenTypeFunctionCallable.makeInterfaceName(method.getReturnType(), method.getParameterTypes());
        cw.visitField(ACC_PUBLIC, DelegatedExpressionCallStatic.getWrapperName(method), "Ljava/lang/Object;", null, null);
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, method.getMethod()
                                                            .getName(), Type.getMethodDescriptor(method.getMethod()), null, null);
        mw.visitCode();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, name, DelegatedExpressionCallStatic.getWrapperName(method), "Ljava/lang/Object;");
        Label runZs = new Label();
        mw.visitJumpInsn(IFNONNULL, runZs);
        mw.visitVarInsn(ALOAD, 0);
        int varIndex = 1;
        for (ZenType parameterType : method.getParameterTypes()) {
            Type parameterASMType = parameterType.toASMType();
            mw.visitVarInsn(parameterASMType.getOpcode(ILOAD), varIndex);
            varIndex += parameterASMType.getSize();
        }
        varIndex = 1;
        mw.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(method.getOwner()), method.getMethod()
                                                                                         .getName(), Type.getMethodDescriptor(method.getMethod()), method.getOwner()
                                                                                                                                                         .isInterface());
        mw.visitInsn(method.getReturnType().toASMType().getOpcode(IRETURN));
        mw.visitLabel(runZs);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, name, DelegatedExpressionCallStatic.getWrapperName(method), Type.getDescriptor(Object.class));
        mw.visitTypeInsn(CHECKCAST, functionItfName);
        for (ZenType parameterType : method.getParameterTypes()) {
            Type parameterASMType = parameterType.toASMType();
            mw.visitVarInsn(parameterASMType.getOpcode(ILOAD), varIndex);
            varIndex += parameterASMType.getSize();
        }
        mw.visitMethodInsn(INVOKEINTERFACE, functionItfName, "accept", Type.getMethodDescriptor(method.getMethod()), true);
        mw.visitInsn(method.getReturnType().toASMType().getOpcode(IRETURN));
        mw.visitMaxs(0, 0);
        mw.visitEnd();
    }

    private void compileOuterImplements(OuterImplementsInfo outerImplementsInfo) {
        List<Method> methods = outerImplementsInfo.getMethods();
        for (Method method : methods) {
            StringBuilder methodSignatureBuf = new StringBuilder();
            methodSignatureBuf.append('(');
            Class<?>[] parameterTypes = method.getParameterTypes();
            Arrays.stream(parameterTypes)
                    .skip(1)
                    .map(Type::getDescriptor)
                    .forEach(methodSignatureBuf::append);
            methodSignatureBuf.append(')');
            methodSignatureBuf.append(Type.getDescriptor(method.getReturnType()));
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, method.getName(), methodSignatureBuf.toString(), null, null);
            mw.visitCode();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                mw.visitVarInsn(Type.getType(parameterType).getOpcode(ILOAD), i);
            }
            mw.visitMethodInsn(INVOKESTATIC, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);
            mw.visitInsn(Type.getType(method.getReturnType()).getOpcode(IRETURN));
            mw.visitMaxs(0, 0);
            mw.visitEnd();
        }
    }

    protected static class OuterImplementsInfo {
        private final Class<?> itf;
        private final List<Method> methods;

        public OuterImplementsInfo(Class<?> itf, List<Method> methods) {
            this.itf = itf;
            this.methods = methods;
        }

        public Class<?> getItf() {
            return itf;
        }

        public List<Method> getMethods() {
            return methods;
        }
    }
}
