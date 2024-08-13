package youyihj.zenutils.impl.member.bytecode;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.StableType;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.member.reflect.ReflectionClassData;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class BytecodeTypeData implements TypeData {
    private final String descriptor;
    private final String genericInfo;
    private final BytecodeClassDataFetcher classDataFetcher;

    /* package-private */ BytecodeTypeData(String descriptor, String genericInfo, BytecodeClassDataFetcher classDataFetcher) {
        this.descriptor = descriptor;
        this.genericInfo = genericInfo;
        this.classDataFetcher = classDataFetcher;
    }

    @Override
    public Type javaType() {
        return new StableType(this);
    }

    @Override
    public String descriptor() {
        return descriptor;
    }

    @Override
    public ClassData asClassData() {
        try {
            if (descriptor.startsWith("L")) {
                return classDataFetcher.forName(descriptor.substring(1, descriptor.length() - 1));
            }
            // TODO: bad implementation
            if (descriptor.startsWith("[")) {
                return new ReflectionClassData(Array.newInstance(Object.class, new int[descriptor.lastIndexOf('[') + 1]).getClass());
            }
            return classDataFetcher.forName(descriptor);
        } catch (ClassNotFoundException e) {
            return classDataFetcher.forClass(Object.class);
        }
    }

    @Override
    public String toString() {
        return genericInfo != null ? genericInfo : descriptor;
    }
}
