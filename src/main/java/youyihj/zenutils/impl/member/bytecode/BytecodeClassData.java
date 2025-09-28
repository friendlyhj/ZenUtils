package youyihj.zenutils.impl.member.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import youyihj.zenutils.impl.member.*;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author youyihj
 */
public class BytecodeClassData extends BytecodeAnnotatedMember implements ClassData {
    private final byte[] bytecode;
    private final BytecodeClassDataFetcher classDataFetcher;
    private final ClassNode classNode;

    private final Map<LookupRequester, List<FieldData>> fieldsCache = new EnumMap<>(LookupRequester.class);
    private final Map<LookupRequester, List<ExecutableData>> methodsCache = new EnumMap<>(LookupRequester.class);
    private final Map<LookupRequester, List<ExecutableData>> constructorsCache = new EnumMap<>(LookupRequester.class);

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
    public List<FieldData> fields(LookupRequester requester) {
        return fieldsCache.computeIfAbsent(requester, this::fields0);
    }

    private List<FieldData> fields0(LookupRequester requester) {
        List<FieldData> fieldData = new ArrayList<>();
        for (FieldNode field : classNode.fields) {
            if (requester.allows(field.access)) {
                fieldData.add(new BytecodeFieldData(field, this));
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            fieldData.addAll(superClass.fields(requester));
        }
        return fieldData;
    }

    @Override
    public List<ExecutableData> methods(LookupRequester requester) {
        return methodsCache.computeIfAbsent(requester, this::methods0);
    }

    private List<ExecutableData> methods0(LookupRequester requester) {
        List<ExecutableData> methods = new ArrayList<>();
        Set<String> usedDescriptions = new HashSet<>();
        for (MethodNode method : classNode.methods) {
            if (!method.name.startsWith("<") && requester.allows(method.access)) {
                BytecodeMethodData methodData = new BytecodeMethodData(method, this);
                methods.add(methodData);
                usedDescriptions.add(methodData.name() + methodData.descriptorWithoutReturnType());
            }
        }
        ClassData superClass = superClass();
        if (superClass != null) {
            for (ExecutableData superMethod : superClass.methods(requester)) {
                if (usedDescriptions.add(superMethod.name() + superMethod.descriptorWithoutReturnType())) {
                    methods.add(superMethod);
                }
            }
        }
        for (ClassData anInterface : interfaces()) {
            List<ExecutableData> interfaceMethods = anInterface.methods(requester);
            for (ExecutableData interfaceMethod : interfaceMethods) {
                if (usedDescriptions.add(interfaceMethod.name() + interfaceMethod.descriptorWithoutReturnType())) {
                    methods.add(interfaceMethod);
                }
            }
        }
        return methods;
    }

    @Override
    public List<ExecutableData> constructors(LookupRequester requester) {
        return constructorsCache.computeIfAbsent(requester, this::constructors0);
    }

    private List<ExecutableData> constructors0(LookupRequester requester) {
        List<ExecutableData> constructors = new ArrayList<>();
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>") && requester.allows(method.access)) {
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
