package youyihj.zenutils.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author youyihj
 */
public class ReflectUtils {
    public static Field removePrivateFinal(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        field.setAccessible(true);
        modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
        return field;
    }
}
