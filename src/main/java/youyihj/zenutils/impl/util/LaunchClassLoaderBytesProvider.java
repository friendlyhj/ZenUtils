package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import youyihj.zenutils.Reference;
import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;
import youyihj.zenutils.impl.member.bytecode.ClassExcludedException;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author youyihj
 */
public class LaunchClassLoaderBytesProvider implements ClassBytesProvider {
    private static final MethodHandle RUN_TRANSFORMERS;
    private static final MethodHandle TRANSFORM_NAME;
    private static final MethodHandle UNTRANSFORM_NAME;

    private static final Collection<String> TRANSFORMER_EXCEPTIONS;

    static {
        try {
            Class<?> classLoaderClass = Reference.IS_CLEANROOM ? Class.forName("top.outlands.foundation.boot.ActualClassLoader") : LaunchClassLoader.class;
            Field lookup$impl_lookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup$impl_lookup.setAccessible(true);
            MethodHandles.Lookup implLookup = (MethodHandles.Lookup) lookup$impl_lookup.get(null);
            MethodHandles.Lookup lclLookup = implLookup.in(classLoaderClass);

            RUN_TRANSFORMERS = lclLookup.findVirtual(classLoaderClass, "runTransformers", MethodType.methodType(byte[].class, String.class, String.class, byte[].class));
            TRANSFORM_NAME = lclLookup.findVirtual(classLoaderClass, "transformName", MethodType.methodType(String.class, String.class));
            UNTRANSFORM_NAME = lclLookup.findVirtual(classLoaderClass, "untransformName", MethodType.methodType(String.class, String.class));

            TRANSFORMER_EXCEPTIONS = InternalUtils.cast(Reference.IS_CLEANROOM ?
                    lclLookup.findVirtual(classLoaderClass, "getTransformerExclusions", MethodType.methodType(List.class)).invoke(Launch.classLoader) :
                    lclLookup.findGetter(classLoaderClass, "transformerExceptions", Set.class).invoke(Launch.classLoader));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException {
        // minecraft obfuscated classes do not have package names
        // and I don't believe any mod class would be in default package
        if (!className.contains(".")) {
            throw new ClassExcludedException(className);
        }
        // skip package-info and module-info
        if (className.endsWith("-info")) {
            throw new ClassExcludedException(className);
        }
        LaunchClassLoader classLoader = Launch.classLoader;
        try {
            if (!shouldTransform(className)) {
                byte[] classBytes = classLoader.getClassBytes(className);
                if (classBytes == null) {
                    throw new ClassNotFoundException(className);
                }
                return classBytes;
            }
            String transformName = TRANSFORM_NAME.invoke(classLoader, className).toString();
            String untransformName = UNTRANSFORM_NAME.invoke(classLoader, className).toString();
            byte[] untransformedBytecodes = classLoader.getClassBytes(untransformName);
            if (untransformedBytecodes == null) {
                throw new ClassNotFoundException(className);
            }
            return (byte[]) RUN_TRANSFORMERS.invoke(classLoader, untransformName, transformName, untransformedBytecodes);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new ClassNotFoundException(className);
        }
    }

    private boolean shouldTransform(String className) {
        if (className.startsWith("java.")) {
            return false;
        }
        for (String exception : TRANSFORMER_EXCEPTIONS) {
            if (className.startsWith(exception)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
