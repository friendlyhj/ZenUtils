package youyihj.zenutils.api.zenscript;

import youyihj.zenutils.impl.zenscript.nat.NativeClassValidate;

/**
 * @author youyihj
 */
public interface INativeClassExclude {
    boolean shouldExclude(Class<?> clazz);

    static void register(INativeClassExclude exclude) {
        NativeClassValidate.EXCLUDES.add(exclude);
    }

    static void filterPackage(String name) {
        register(clazz -> clazz.getCanonicalName().startsWith(name));
    }
}
