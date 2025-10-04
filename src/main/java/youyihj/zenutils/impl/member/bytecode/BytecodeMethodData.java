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

    private TypeData returnType;
    private List<TypeData> parameters;

    public BytecodeMethodData(MethodNode methodNode, BytecodeClassData declaringClass) {
        this.methodNode = methodNode;
        this.declaringClass = declaringClass;
        setAnnotationNodes(methodNode.visibleAnnotations);
        setAnnotationNodes(methodNode.invisibleAnnotations);
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
        if (returnType == null) {
            String signature = methodNode.signature;
            String descriptor = Type.getReturnType(methodNode.desc).getDescriptor();
            if (signature == null) {
                returnType = declaringClass.fetcher().type(descriptor, null);
            } else {
                String returnAndException = signature.substring(signature.indexOf(')') + 1);
                if (returnAndException.contains("^")) {
                    returnType = declaringClass.fetcher().type(descriptor, signature.substring(0, signature.indexOf('^')));
                } else {
                    returnType = declaringClass.fetcher().type(descriptor, returnAndException);
                }
            }
        }
        return returnType;
    }

    @Override
    public List<TypeData> parameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
            if (methodNode.signature == null) {
                for (Type parameter : Type.getMethodType(methodNode.desc).getArgumentTypes()) {
                    parameters.add(declaringClass.fetcher().type(parameter.getDescriptor(), null));
                }
            } else {
                List<String> genericInfos = new MethodParameterParser(methodNode.signature).parse();
                Type[] descriptors = Type.getMethodType(methodNode.desc).getArgumentTypes();
                for (int i = 0; i < descriptors.length; i++) {
                    parameters.add(declaringClass.fetcher().type(descriptors[i].getDescriptor(), genericInfos.get(i)));
                }
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

    @Override
    public String descriptorWithoutReturnType() {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Type parameter : Type.getMethodType(methodNode.desc).getArgumentTypes()) {
            buf.append(parameter.getDescriptor());
        }
        buf.append(')');
        return buf.toString();
    }
}
