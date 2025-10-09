package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import youyihj.zenutils.Reference;
import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;

import java.lang.reflect.Method;

/**
 * @author youyihj
 */
public class LaunchClassLoaderBytesProvider implements ClassBytesProvider {
    private final Method runTransformersMethod;
    private final Method transfromNameMethod;
    private final Method untransformNameMethod;

    public LaunchClassLoaderBytesProvider() {
        try {
            Class<?> classLoaderClass = Reference.IS_CLEANROOM ? Class.forName("top.outlands.foundation.boot.ActualClassLoader") : LaunchClassLoader.class;
            this.runTransformersMethod = classLoaderClass.getDeclaredMethod("runTransformers", String.class, String.class, byte[].class);
            this.runTransformersMethod.setAccessible(true);
            this.transfromNameMethod = classLoaderClass.getDeclaredMethod("transformName", String.class);
            this.transfromNameMethod.setAccessible(true);
            this.untransformNameMethod = classLoaderClass.getDeclaredMethod("untransformName", String.class);
            this.untransformNameMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException {
        LaunchClassLoader classLoader = Launch.classLoader;
        try {
            String transformName = transfromNameMethod.invoke(classLoader, className).toString();
            // obfuscated
            if (!transformName.equals(className)) {
                throw new ClassNotFoundException(className);
            }
            String untransformName = untransformNameMethod.invoke(classLoader, className).toString();
            byte[] untransformedBytecodes = classLoader.getClassBytes(untransformName);
            if (untransformedBytecodes == null) {
                throw new ClassNotFoundException(className);
            }
            return (byte[]) runTransformersMethod.invoke(classLoader, untransformName, transformName, untransformedBytecodes);
        } catch (Exception e) {
            throw new ClassNotFoundException(className);
        }
    }
}
