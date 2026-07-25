package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import net.minecraftforge.fml.common.patcher.ClassPatchManager;
import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;
import youyihj.zenutils.impl.member.bytecode.ClassExcludedException;
import youyihj.zenutils.impl.member.bytecode.ClasspathBytesProvider;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class DeobfMinecraftBytesProvider implements ClassBytesProvider {
    private final DeobfuscationTransformer deobf = new DeobfuscationTransformer();
    private final ClassBytesProvider minecraftLib;

    public DeobfMinecraftBytesProvider() {
        List<Path> classPath = new ArrayList<>();
        for (URL source : Launch.classLoader.getSources()) {
            try {
                classPath.add(Paths.get(source.toURI()));
            } catch (Exception ignored) {}
        }

        minecraftLib = new ClasspathBytesProvider(classPath);
    }

    @Override
    public byte[] getClassBytes(String className) throws ClassNotFoundException, ClassExcludedException {
        if (!className.contains(".")) {
            throw new ClassExcludedException(className);
        }
        String obfName = deobf.unmapClassName(className);
        return deobf.transform(obfName, className, ClassPatchManager.INSTANCE.applyPatch(obfName, className, minecraftLib.getClassBytes(obfName)));
    }

    @Override
    public void close() throws IOException {
        minecraftLib.close();
    }
}
