package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import net.minecraftforge.fml.common.patcher.ClassPatchManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.impl.member.bytecode.ClassBytesProvider;
import youyihj.zenutils.impl.member.bytecode.ClassExcludedException;
import youyihj.zenutils.impl.member.bytecode.ClasspathBytesProvider;
import zone.rong.mixinbooter.util.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author youyihj
 */
public class DeobfMinecraftBytesProvider implements ClassBytesProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    private final DeobfuscationTransformer deobf = new DeobfuscationTransformer();
    private final ClassBytesProvider minecraftLib;

    public DeobfMinecraftBytesProvider() {
        minecraftLib = new ClasspathBytesProvider(getClassPath());
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

    private static List<Path> getClassPath() {
        List<Path> paths = new ArrayList<>();

        if (Environment.inDev() || Environment.side().equals("CLIENT")) {
            List<URL> urLs = Launch.classLoader.getSources();
            for (URL url : urLs) {
                if (!url.getProtocol().equals("file"))
                    continue;

                try {
                    LOGGER.info("Loading classpath from client: {}", url.toExternalForm());
                    paths.add(Paths.get(url.toURI()));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            // server
            String forgeClassPath = System.getProperty("java.class.path");
            try (JarFile forgeJar = new JarFile(forgeClassPath)) {
                String path = forgeJar.getManifest().getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
                for (StringTokenizer st = new StringTokenizer(path); st.hasMoreTokens(); ) {
                    String elt = st.nextToken();
                    LOGGER.info("Loading classpath from server: {}", elt);
                    paths.add(Paths.get(elt));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return paths;
    }
}
