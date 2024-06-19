package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.*;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenTypeUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
public abstract class ReloadableCustomClass {
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
        cw.visit(V1_8, ACC_PUBLIC, name, null, Type.getInternalName(superClass), interfaces.stream()
                                                                                           .map(Type::getInternalName)
                                                                                           .toArray(String[]::new));
        for (Constructor<?> constructor : superClass.getConstructors()) {
            compileConstructor(constructor);
        }
        Stream.concat(Stream.of(superClass), interfaces.stream())
              .flatMap(it -> Arrays.stream(it.getMethods()))
              .filter(it -> !Modifier.isFinal(it.getModifiers()))
              .map(it -> new JavaMethod(it, ZenTypeUtil.EMPTY_REGISTRY))
              .forEach(this::compileMethod);
        cw.visitEnd();
        return cw.toByteArray();
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
        cw.visitField(ACC_PUBLIC, DelegatedExpressionCallStatic.getWrapperName(method), Type.getInternalName(Object.class), null, null);
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, method.getMethod()
                                                            .getName(), Type.getMethodDescriptor(method.getMethod()), null, null);
        mw.visitCode();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, name, DelegatedExpressionCallStatic.getWrapperName(method), Type.getDescriptor(Object.class));
        mw.visitTypeInsn(INSTANCEOF, functionItfName);
        Label runZs = new Label();
        mw.visitJumpInsn(IFNE, runZs);
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

    protected static class OuterImplementsInfo {
        private final Class<?> itf;
        private final List<Method> methods;

        public OuterImplementsInfo(Class<?> itf, List<Method> methods) {
            this.itf = itf;
            this.methods = methods;
        }
    }
}
