package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.zenscript.INativeClassExclude;
import youyihj.zenutils.impl.member.ClassData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author youyihj
 */
public class NativeClassValidate {
    public static final List<INativeClassExclude> EXCLUDES = new ArrayList<>();
    private static final List<String> WHITELIST = ImmutableList.of(
            Integer.class.getName(),
            Long.class.getName(),
            Short.class.getName(),
            Double.class.getName(),
            Float.class.getName(),
            Boolean.class.getName(),
            Byte.class.getName(),
            Object.class.getName()
    );

    static {
        Lists.newArrayList(
                "java.io",
                "java.nio",
                "java.awt",
                "java.applet",
                "java.rmi",
                "java.net",
                "java.lang",
                "java.security",
                "java.util.zip",
                "java.util.concurrent",
                "java.util.logging",
                "scala",
                "kotlin",
                "crafttweaker",
                "com.teamacronymcoders.contenttweaker",
                "org.apache.commons.io",
                "org.apache.http",
                "org.apache.logging",
                "io.netty",
                "org.spongepowered.asm",
                "org.objectweb.asm",
                "sun.",
                "youyihj.zenutils.impl",
                "jdk",
                "javax",
                "groovy",
                "com.cleanroommc.groovyscript"
        ).forEach(INativeClassExclude::filterPrefix);
        INativeClassExclude.filterClass(Scanner.class);
    }

    public static boolean isValid(ClassData clazz) {
        return WHITELIST.contains(clazz.name()) || (!clazz.isAnnotationPresent(ZenClass.class) && EXCLUDES.stream().noneMatch(it -> it.shouldExclude(clazz)));
    }
}
