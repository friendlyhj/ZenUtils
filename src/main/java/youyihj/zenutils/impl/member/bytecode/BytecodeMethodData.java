package youyihj.zenutils.impl.member.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class BytecodeMethodData extends BytecodeAnnotatedMember implements ExecutableData {
    private final MethodNode methodNode;
    private final BytecodeClassData declaringClass;

    public BytecodeMethodData(MethodNode methodNode, BytecodeClassData declaringClass) {
        this.methodNode = methodNode;
        this.declaringClass = declaringClass;
        setAnnotationNodes(methodNode.visibleAnnotations);
    }

    @Override
    public String name() {
        return methodNode.name;
    }

    @Override
    public ClassData declaringClass() {
        return declaringClass;
    }

    @Override
    public int parameterCount() {
        return Type.getMethodType(methodNode.desc).getArgumentTypes().length;
    }

    @Override
    public int modifiers() {
        return methodNode.access;
    }

    @Override
    public TypeData returnType() {
        String signature = methodNode.signature;
        String descriptor = Type.getReturnType(methodNode.desc).getDescriptor();
        if (signature == null) {
            return declaringClass.getClassDataFetcher().type(descriptor, null);
        } else {
            String returnAndException = signature.substring(signature.indexOf(')') + 1);
            if (returnAndException.contains("^")) {
                return declaringClass.getClassDataFetcher().type(descriptor, signature.substring(0, signature.indexOf('^')));
            } else {
                return declaringClass.getClassDataFetcher().type(descriptor, returnAndException);
            }
        }
    }

    @Override
    public List<TypeData> parameters() {
        List<TypeData> parameters = new ArrayList<>();
        if (methodNode.signature == null) {
            for (Type parameter : Type.getMethodType(methodNode.desc).getArgumentTypes()) {
                parameters.add(declaringClass.getClassDataFetcher().type(parameter.getDescriptor(), null));
            }
        } else {
            List<String> genericInfos = new MethodParameterParser(methodNode.signature).parse();
            Type[] descriptors = Type.getMethodType(methodNode.desc).getArgumentTypes();
            for (int i = 0; i < descriptors.length; i++) {
                parameters.add(declaringClass.getClassDataFetcher().type(descriptors[i].getDescriptor(), genericInfos.get(i)));
            }
        }
        return parameters;
    }

    @Override
    public boolean isVarArgs() {
        return (methodNode.access & Opcodes.ACC_VARARGS) != 0;
    }

    @Override
    public String descriptor() {
        return methodNode.desc;
    }
}
