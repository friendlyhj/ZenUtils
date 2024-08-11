package youyihj.zenutils.impl.member;

import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public interface TypeData {
    Type javaType();

    String descriptor();

    ClassData asClassData();
}
