package youyihj.zenutils.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author youyihj
 */
public class ReflectUtils {
    public static Field removePrivateFinal(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        field.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        return field;
    }

    public static Field removePrivate(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static <T> List<T> getAllFieldsWithClass(Class<?> target, Class<T> fieldClass, Object targetInstance) throws IllegalAccessException, IllegalArgumentException, NullPointerException {
        List<T> temp = new ArrayList<>();
        for (Field field : target.getDeclaredFields()) {
            if (fieldClass.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                Object o = field.get(targetInstance);
                temp.add(fieldClass.cast(o));
            }
        }
        return temp;
    }

    public static Class<?> findCommonSuperclass(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            return null;
        }

        for (Class<?> cls : classes) {
            if (cls == null || cls.isPrimitive()) {
                return null;
            }
        }

        List<Set<Class<?>>> allSuperSets = new ArrayList<>();
        for (Class<?> cls : classes) {
            allSuperSets.add(getAllTypes(cls));
        }

        Set<Class<?>> common = new HashSet<>(allSuperSets.get(0));
        for (int i = 1; i < allSuperSets.size(); i++) {
            common.retainAll(allSuperSets.get(i));
            if (common.isEmpty()) {
                return null;
            }
        }

        common.remove(Object.class);
        if (common.isEmpty()) {
            return null;
        }

        Class<?> best = null;
        int maxDepth = -1;
        for (Class<?> candidate : common) {
            int depth = getInheritanceDepth(candidate);
            if (depth > maxDepth) {
                maxDepth = depth;
                best = candidate;
            }
        }

        return best;
    }

    private static Set<Class<?>> getAllTypes(Class<?> cls) {
        Set<Class<?>> res = new HashSet<>();
        Queue<Class<?>> queue = new ArrayDeque<>();
        queue.add(cls);
        while (!queue.isEmpty()) {
            Class<?> current = queue.poll();
            if (res.add(current)) {
                Class<?> sup = current.getSuperclass();
                if (sup != null) {
                    queue.add(sup);
                }
                queue.addAll(Arrays.asList(current.getInterfaces()));
            }
        }
        return res;
    }

    private static int getInheritanceDepth(Class<?> candidate) {
        return getClassDepth(candidate) + getInterfaceDepth(candidate, new HashSet<>());
    }

    private static int getClassDepth(Class<?> cls) {
        int d = 0;
        while ((cls = cls.getSuperclass()) != null) {
            d++;
        }
        return d;
    }

    private static int getInterfaceDepth(Class<?> itf, Set<Class<?>> seen) {
        if (!itf.isInterface() || !seen.add(itf)) {
            return 0;
        }
        int max = 0;
        for (Class<?> sup : itf.getInterfaces()) {
            max = Math.max(max, 1 + getInterfaceDepth(sup, seen));
        }
        return max;
    }
}
