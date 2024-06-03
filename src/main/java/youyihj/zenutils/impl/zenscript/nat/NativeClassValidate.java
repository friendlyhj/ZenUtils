package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.Lists;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.zenscript.INativeClassExclude;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class NativeClassValidate {
    public static final List<INativeClassExclude> EXCLUDES = new ArrayList<>();

    public static final boolean ENABLE = true;

    static {
        Lists.newArrayList(
                "java.io",
                "java.nio",
                "java.awt",
                "java.applet",
                "java.rmi",
                "java.net",
                "java.lang",
                "java.util.zip",
                "java.util.concurrent",
                "java.util.logging",
                "java.util.Random",
                "java.util.Scanner",
                "scala",
                "kotlin",
                "org.apache.commons.io",
                "org.apache.http",
                "io.netty",
                "org.spongepowered.asm",
                "org.objectweb.asm",
                "sun",
                "youyihj.zenutils.impl",
                "jdk",
                "javax"
        ).forEach(INativeClassExclude::filterPackage);
    }

    public static boolean isValid(Class<?> clazz) {
        return ENABLE && !clazz.isAnnotationPresent(ZenClass.class) && EXCLUDES.stream().noneMatch(it -> it.shouldExclude(clazz));
    }
}
