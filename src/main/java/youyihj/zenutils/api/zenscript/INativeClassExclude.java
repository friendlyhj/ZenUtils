package youyihj.zenutils.api.zenscript;

import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.zenscript.nat.NativeClassValidate;

/**
 * @author youyihj
 */
public interface INativeClassExclude {
    boolean shouldExclude(ClassData clazz);

    static void register(INativeClassExclude exclude) {
        NativeClassValidate.EXCLUDES.add(exclude);
    }

    static void filterPrefix(String prefix) {
        register(clazz -> clazz.name().startsWith(prefix));
    }

    static void filterClass(Class<?> clazz) {
        register(it -> it.name().equals(clazz.getCanonicalName()));
    }
}
