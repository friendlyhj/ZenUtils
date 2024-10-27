package youyihj.zenutils.impl.member.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.FieldData;
import youyihj.zenutils.impl.member.LiteralType;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author youyihj
 */
public class BytecodeClassData extends BytecodeAnnotatedMember implements ClassData {
    private final byte[] bytecode;
    private final BytecodeClassDataFetcher classDataFetcher;
    private final ClassNode classNode;

    public BytecodeClassData(byte[] bytecode, BytecodeClassDataFetcher classDataFetcher) {
        this.bytecode = bytecode;
        this.classDataFetcher = classDataFetcher;
        this.classNode = new ClassNode();
        new ClassReader(bytecode).accept(classNode, ClassReader.SKIP_CODE);
        setAnnotationNodes(classNode.visibleAnnotations);
        setAnnotationNodes(classNode.invisibleAnnotations);
    }

    @Override
    public String name() {
        return classNode.name.replace('/', '.');
    }

    @Override
    public String internalName() {
        return classNode.name;
    }

    @Override
    public List<FieldData> fields(boolean publicOnly) {
        List<FieldData> fieldData = new ArrayList<>();
        for (FieldNode field : classNode.fields) {
            if (!publicOnly || Modifier.isPublic(field.access)) {
                fieldData.add(new BytecodeFieldData(field, this));
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            for (FieldData superField : superClass.fields(false)) {
                int modifiers = superField.modifiers();
                if (Modifier.isPublic(modifiers) || (!publicOnly && Modifier.isProtected(modifiers))) {
                    fieldData.add(superField);
                }
            }
        }
        return fieldData;
    }

    @Override
    public List<ExecutableData> methods(boolean publicOnly) {
        List<ExecutableData> methods = new ArrayList<>();
        Set<String> usedDescriptions = new HashSet<>();
        for (MethodNode method : classNode.methods) {
            if (!method.name.startsWith("<") && (!publicOnly || Modifier.isPublic(method.access))) {
                BytecodeMethodData methodData = new BytecodeMethodData(method, this);
                methods.add(methodData);
                usedDescriptions.add(methodData.name() + methodData.descriptor());
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            for (ExecutableData superMethod : superClass.methods(false)) {
                int modifiers = superMethod.modifiers();
                if (Modifier.isPublic(modifiers) || (!publicOnly && Modifier.isProtected(modifiers))) {
                    if (usedDescriptions.add(superMethod.name() + superMethod.descriptor())) {
                        methods.add(superMethod);
                    }
                }
            }
        }
        for (ClassData anInterface : interfaces()) {
            List<ExecutableData> interfaceMethods = anInterface.methods(true);
            for (ExecutableData interfaceMethod : interfaceMethods) {
                if (usedDescriptions.add(interfaceMethod.name() + interfaceMethod.descriptor())) {
                    methods.add(interfaceMethod);
                }
            }
        }
        return methods;
    }

    @Override
    public List<ExecutableData> constructors(boolean publicOnly) {
        List<ExecutableData> constructors = new ArrayList<>();
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>") && (!publicOnly || Modifier.isPublic(method.access))) {
                constructors.add(new BytecodeMethodData(method, this));
            }
        }
        return constructors;
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(classNode.access);
    }

    @Override
    public boolean isAssignableFrom(ClassData classData) {
        if (name().equals(classData.name())) {
            return true;
        }
        List<ClassData> interfaces = classData.interfaces();
        for (ClassData anInterface : interfaces) {
            if (name().equals(anInterface.name())) {
                return true;
            }
        }
        ClassData superClass = classData.superClass();
        if (superClass != null) {
            return isAssignableFrom(superClass);
        }
        return false;
    }

    @Nullable
    @Override
    public ClassData superClass() {
        try {
            return classDataFetcher.forName(classNode.superName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<ClassData> interfaces() {
        List<ClassData> interfaces = new ArrayList<>();
        for (String anInterface : classNode.interfaces) {
            try {
                interfaces.add(classDataFetcher.forName(anInterface.replace('/', '.')));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return interfaces;
    }

    @Override
    public Type javaType() {
        return new LiteralType(this);
    }

    @Override
    public String descriptor() {
        return "L" + internalName() + ";";
    }

    @Override
    public ClassData asClassData() {
        return this;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public BytecodeClassDataFetcher getClassDataFetcher() {
        return classDataFetcher;
    }

    @Override
    public String toString() {
        return descriptor();
    }
}
