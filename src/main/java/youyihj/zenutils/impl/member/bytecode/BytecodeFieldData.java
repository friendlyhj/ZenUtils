package youyihj.zenutils.impl.member.bytecode;

import org.objectweb.asm.tree.FieldNode;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.TypeData;

/**
 * @author youyihj
 */
public class BytecodeFieldData extends BytecodeAnnotatedMember implements FieldData {
    private final FieldNode fieldNode;
    private final BytecodeClassData declaringClass;

    public BytecodeFieldData(FieldNode fieldNode, BytecodeClassData declaringClass) {
        this.fieldNode = fieldNode;
        this.declaringClass = declaringClass;
    }

    @Override
    public ClassData declaringClass() {
        return declaringClass;
    }

    @Override
    public String name() {
        return fieldNode.name;
    }

    @Override
    public int modifiers() {
        return fieldNode.access;
    }

    @Override
    public TypeData type() {
        return declaringClass.getClassDataFetcher().type(fieldNode.desc, fieldNode.signature);
    }
}
