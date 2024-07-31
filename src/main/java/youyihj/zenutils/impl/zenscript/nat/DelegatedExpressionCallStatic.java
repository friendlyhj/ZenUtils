package youyihj.zenutils.impl.zenscript.nat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
public class DelegatedExpressionCallStatic extends ExpressionCallStatic {
    private final ExpressionCallStatic interfaceStaticMethodWrapper;
    private final String wrapperClassName;
    private final JavaMethod method;

    public DelegatedExpressionCallStatic(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, Expression... arguments) {
        super(position, environment, method, arguments);
        if (method instanceof JavaMethod && ((JavaMethod) method).getOwner().isInterface()) {
            this.method = (JavaMethod) method;
            this.wrapperClassName = getWrapperName(this.method);
            this.interfaceStaticMethodWrapper = new ExpressionCallStatic(position, environment, JavaMethod.getStatic(this.wrapperClassName, "invoke", method.getReturnType(), method.getParameterTypes()));
        } else {
            this.interfaceStaticMethodWrapper = null;
            this.wrapperClassName = null;
            this.method = null;
        }
    }

    public static String getWrapperName(JavaMethod method) {
        return WrappedMethodNameGenerator.INSTANCE.get(method.getMethod());
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if (interfaceStaticMethodWrapper == null) {
            super.compile(result, environment);
        } else {
            interfaceStaticMethodWrapper.compile(result, environment);
            if (!environment.containsClass(wrapperClassName)) {
                environment.putClass(wrapperClassName, compileWrapperClass(method));
            }
        }
    }

    private byte[] compileWrapperClass(JavaMethod method) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, wrapperClassName, null, "java/lang/Object", null);
        MethodVisitor constructor = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(-1, -1);
        constructor.visitEnd();
        MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "invoke", Type.getMethodDescriptor(method.getMethod()), null, null);
        int varIndex = 0;
        for (ZenType parameterType : method.getParameterTypes()) {
            Type asmType = parameterType.toASMType();
            invoke.visitVarInsn(asmType.getOpcode(ILOAD), varIndex);
            varIndex += asmType.getSize();
        }
        invoke.visitMethodInsn(INVOKESTATIC, Type.getInternalName(method.getOwner()), method.getMethod().getName(), Type.getMethodDescriptor(method.getMethod()), true);
        invoke.visitInsn(method.getReturnType().toASMType().getOpcode(IRETURN));
        invoke.visitMaxs(-1, -1);
        invoke.visitEnd();
        return cw.toByteArray();
    }
}
