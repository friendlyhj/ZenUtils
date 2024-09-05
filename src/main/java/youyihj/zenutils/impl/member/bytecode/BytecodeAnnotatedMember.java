package youyihj.zenutils.impl.member.bytecode;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import youyihj.zenutils.impl.member.AnnotatedMember;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.Type.*;

/**
 * @author youyihj
 */
public class BytecodeAnnotatedMember implements AnnotatedMember {
    private final List<AnnotationNode> annotationNodes = new ArrayList<>();

    protected BytecodeAnnotatedMember() {
    }

    protected void setAnnotationNodes(List<AnnotationNode> annotationNodes) {
        if (annotationNodes != null) {
            this.annotationNodes.addAll(annotationNodes);
        }
    }

    @Override
    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass) {
        for (AnnotationNode annotation : annotationNodes) {
            if (Objects.equals(annotation.desc, Type.getDescriptor(annotationClass))) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        for (AnnotationNode annotation : annotationNodes) {
            if (Objects.equals(annotation.desc, Type.getDescriptor(annotationClass))) {
                ClassLoader annotationClassLoader = annotationClass.getClassLoader();
                return (A) Proxy.newProxyInstance(annotationClassLoader, new Class[]{annotationClass}, ((proxy, method, args) -> {
                    List<Object> values = annotation.values;
                    if (values != null) {
                        for (int i = 0; i < values.size(); i += 2) {
                            if (Objects.equals(values.get(i), method.getName())) {
                                return parseAnnotationValue(values.get(i + 1), annotationClassLoader);
                            }
                        }
                    }
                    if (method.getName().equals("annotationType")) {
                        return annotationClass;
                    }
                    return method.getDefaultValue();
                }));
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object parseAnnotationValue(Object value, ClassLoader classLoader) throws Exception {
        if (value.getClass() == Type.class) {
            return convertASMType(((Type) value), classLoader);
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            Object[] values = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                values[i] = parseAnnotationValue(list.get(i), classLoader);
            }
            return values;
        }
        if (value instanceof String[]) {
            String[] strings = (String[]) value;
            return Enum.valueOf(((Class<Enum>) Class.forName(Type.getType(strings[0]).getClassName(), true, classLoader)), strings[1]);
        }
        return value;
    }

    private Class<?> convertASMType(Type type, ClassLoader classLoader) {
        try {
            switch (type.getSort()) {
                case VOID:
                    return void.class;
                case BOOLEAN:
                    return boolean.class;
                case BYTE:
                    return byte.class;
                case SHORT:
                    return short.class;
                case INT:
                    return int.class;
                case FLOAT:
                    return float.class;
                case LONG:
                    return long.class;
                case DOUBLE:
                    return double.class;
                case ARRAY:
                    Type elementType = type.getElementType();
                    int dimensions = type.getDimensions();
                    return Array.newInstance(convertASMType(elementType, classLoader), new int[dimensions]).getClass();
                case OBJECT:
                    return Class.forName(type.getClassName(), false, classLoader);
                default:
                    return Object.class;
            }
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }
}
