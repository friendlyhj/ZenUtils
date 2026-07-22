package youyihj.zenutils.impl.core;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;
import zone.rong.mixinbooter.util.Environment;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * @author youyihj
 */
@ReflectionInvoked
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1)
public class ZenUtilsPlugin implements IFMLLoadingPlugin {
    private static final String[] EARLY_CLASS_LOADING_ERROR_TRIGGERS = new String[]{
            "net.minecraft.item.ItemStack",
            "net.minecraft.block.Block",
            "net.minecraft.entity.item.EntityItem",
            "net.minecraftforge.fml.common.FMLCommonHandler",
            "net.minecraftforge.common.MinecraftForge",
            "net.minecraftforge.fml.common.Loader"
    };

    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<>();
        if (Configuration.customScriptEntrypoint.length != 0) {
            transformers.add("youyihj.zenutils.impl.core.FMLModContainerTransformer");
        }
        transformers.add("youyihj.zenutils.impl.config.ClassProvider");
        transformers.add("youyihj.zenutils.impl.core.TConTraitRepresentationTransformer");
        return transformers.toArray(new String[0]);
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (Configuration.enableMixin) {
            try {
                injectZenscriptEngineIntoClassLoader(Launch.classLoader);
                ZenMixin.load();
            } catch (Exception e) {
                CraftTweakerAPI.logError("Failed to load ZenMixin.", e);
            }
        }

        try {
            for (String name : EARLY_CLASS_LOADING_ERROR_TRIGGERS) {
                if (InternalUtils.isClassLoadedOnLCL(name)) {
                    CraftTweakerAPI.logError("Mixin scripts shouldn't execute code (i.e. top level statements) related to Minecraft.");
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public String getAccessTransformerClass() {
        return "youyihj.zenutils.impl.core.ConfigAccessTransformer";
    }

    private void injectZenscriptEngineIntoClassLoader(LaunchClassLoader classLoader) {
        if (Environment.inDev()) {
            return;
        }
        try {
            URL self = ZenUtilsPlugin.class.getProtectionDomain().getCodeSource().getLocation();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(classLoader, findCraftTweakerJar());
            addURL.invoke(classLoader, self);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to add Zenscript Engine into ClassLoader", t);
        }
    }

    private URL findCraftTweakerJar() throws IOException {
        File modDir = new File(Launch.minecraftHome, "mods");
        for (File file : Objects.requireNonNull(modDir.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    if (jarFile.getEntry("META-INF/crafttweaker_at.cfg") != null) {
                        return file.toURI().toURL();
                    }
                }
            }
        }
        throw new IOException("Unable to find Zenscript Engine Jar");
    }
}
