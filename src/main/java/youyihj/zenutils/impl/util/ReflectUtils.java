package youyihj.zenutils.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
}
