package youyihj.zenutils.impl.zenscript.nat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import crafttweaker.api.formatting.IFormatter;
import crafttweaker.api.item.IItemUtils;
import crafttweaker.api.recipes.IBrewingManager;
import crafttweaker.mc1120.item.MCItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.api.zenscript.INativeClassExclude;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.ExecutableData;
import youyihj.zenutils.impl.member.TypeData;
import youyihj.zenutils.impl.member.reflect.ReflectionClassData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author youyihj
 */
public class NativeClassValidate {
    public static final List<INativeClassExclude> EXCLUDES = new ArrayList<>();
    private static final List<String> WHITELIST = ImmutableList.of(
            // primitive types
            Integer.class.getName(),
            Long.class.getName(),
            Short.class.getName(),
            Double.class.getName(),
            Float.class.getName(),
            Boolean.class.getName(),
            Byte.class.getName(),
            Object.class.getName(),
            Enum.class.getName(),
            Comparable.class.getName(),
            Iterable.class.getName(),
            Math.class.getName(), // although you should call crt Math class, it is not accessible in mixin scripts...
            StrictMath.class.getName(),
            StringBuilder.class.getName(), // meh, ~ operator is same
            CharSequence.class.getName(),
            Class.class.getName(), // has extra checks
            Color.class.getName()
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
                "stanhebben.zenscript",
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

        INativeClassExclude.filterClass(IItemUtils.class);
        INativeClassExclude.filterClass(IBrewingManager.class);
        INativeClassExclude.filterClass(IFormatter.class);

        INativeClassExclude.filterClass("com.teamacronymcoders.contenttweaker.modules.materials.brackethandler.MaterialPartDefinition");
        INativeClassExclude.filterClass(MCItemStack.class);
    }

    public static boolean isValid(ClassData clazz, boolean allowZenClasses) {
        return WHITELIST.contains(clazz.name()) || (allowZenClasses || !clazz.isAnnotationPresent(ZenClass.class)) && EXCLUDES.stream().noneMatch(it -> it.shouldExclude(clazz));
    }

    @ReflectionInvoked
    public static Class<?> safeGetClass(Object object) {
        if (object == null) {
            return null;
        }
        ReflectionClassData classData = ReflectionClassData.of(object.getClass());
        while (classData != null && !isValid(classData, false)) {
            classData = classData.superClass();
        }
        return classData == null ? Object.class : ((Class<?>) classData.javaType());
    }

    public static boolean isRiskyClassForName(ExecutableData method) {
        if (!hasReferredType(method.returnType(), "java.lang.Class")) {
            return false;
        }
        for (TypeData type : method.parameters()) {
            if (hasReferredType(type, "java.lang.String")) {
                return true;
            }
        }
        return false;
    }

    // bad impl
    private static boolean hasReferredType(TypeData type, String className) {
        String typeName = type.javaType().getTypeName();
        return typeName.contains(className) || typeName.contains(className.replace('.', '/'));
    }

    public static boolean isValid(ClassData clazz) {
        return isValid(clazz, false);
    }
}
