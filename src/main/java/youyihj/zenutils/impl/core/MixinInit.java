package youyihj.zenutils.impl.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import youyihj.zenutils.api.util.ReflectionInvoked;
import zone.rong.mixinbooter.service.ModDiscoverer;
import zone.rong.mixinbooter.util.Environment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class MixinInit implements IMixinConnector {
    private static final Logger LOGGER = LogManager.getLogger("zenutils-mixin_init");

    @Override
    public void connect() {
        LOGGER.info("Bootstrapping Zenutils Mixin...");
        injectZenscriptEngineIntoClassLoader(Launch.classLoader);
        Mixins.addConfiguration("mixins.zenutils.vanilla.json");
        Mixins.addConfiguration("mixins.zenutils.zenscript.json");
        Mixins.addConfiguration("mixins.zenutils.crafttweaker.json");
        if (Configuration.enableRandomTickEvent) {
            Mixins.addConfiguration("mixins.zenutils.randomtickevent.json");
        }
        if (ModDiscoverer.isModPresent("simpledimensions")) {
            Mixins.addConfiguration("mixins.zenutils.simpledimensions.json");
        }
    }

    private void injectZenscriptEngineIntoClassLoader(LaunchClassLoader classLoader) {
        if (Environment.inDev()) {
            return;
        }
        try {
            URL self = ZenUtilsPlugin.class.getProtectionDomain().getCodeSource().getLocation();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            File craftTweakerJar = findCraftTweakerJar();
            LOGGER.info("found craft tweaker jar: {}", craftTweakerJar);
            addURL.invoke(classLoader, craftTweakerJar.toURI().toURL());
            CoreModManager.getReparseableCoremods().add(craftTweakerJar.getName());
            addURL.invoke(classLoader, self);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to add Zenscript Engine into ClassLoader", t);
        }
    }

    private File findCraftTweakerJar() throws IOException {
        File mcDir = Launch.minecraftHome != null ? Launch.minecraftHome : new File(".");
        File modDir = new File(mcDir, "mods");
        for (File file : Objects.requireNonNull(modDir.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    if (jarFile.getEntry("META-INF/crafttweaker_at.cfg") != null) {
                        return file;
                    }
                }
            }
        }
        throw new IOException("Unable to find Zenscript Engine Jar");
    }
}
