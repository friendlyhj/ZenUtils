/*
 * MIT License
 * <p>
 * Copyright (c) 2022 CleanroomMC
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package youyihj.zenutils.impl.config;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ConfigAnytimeAnytime {
    private static final MethodHandle CONFIGMANAGER$SYNC;

    static {
        try {
            Class.forName("net.minecraftforge.common.config.ConfigManager", true, Launch.classLoader); // Init first
            // Max privilege
            Field lookup$impl_lookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup$impl_lookup.setAccessible(true);
            MethodHandles.Lookup lookup = ((MethodHandles.Lookup) lookup$impl_lookup.get(null)).in(ConfigManager.class);
            CONFIGMANAGER$SYNC = lookup.findStatic(ConfigManager.class, "sync", MethodType.methodType(void.class, Configuration.class, Class.class, String.class, String.class, boolean.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register configuration class that is annotated with {@link Config} here for it to be processed immediately with saving and loading supported.
     * Preferably call this method in a static init block at the very end of your configuration class.
     *
     * @param configClass configuration class that is annotated with {@link Config}
     */
    public static void register(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(Config.class)) {
            return;
        }
        try {
            $register(configClass);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void $register(Class<?> configClass) throws Throwable {
        Field configManager$mod_config_classes = ConfigManager.class.getDeclaredField("MOD_CONFIG_CLASSES");
        Field configManager$configs = ConfigManager.class.getDeclaredField("CONFIGS");
        configManager$mod_config_classes.setAccessible(true);
        configManager$configs.setAccessible(true);
        Map<String, Set<Class<?>>> MOD_CONFIG_CLASSES = (Map<String, Set<Class<?>>>) configManager$mod_config_classes.get(null);
        Map<String, Configuration> CONFIGS = (Map<String, Configuration>) configManager$configs.get(null);

        Config config = configClass.getAnnotation(Config.class);
        String modId = config.modid();

        Set<Class<?>> modConfigClasses = MOD_CONFIG_CLASSES.computeIfAbsent(modId, k -> Sets.newHashSet());
        modConfigClasses.add(configClass);

        File configDir = new File(Launch.minecraftHome, "config");
        String cfgName = config.name();
        if (Strings.isNullOrEmpty(cfgName)) {
            cfgName = modId;
        }
        File configFile = new File(configDir, cfgName + ".cfg");
        String configFileAbsolute = configFile.getAbsolutePath();
        Configuration cfg = CONFIGS.get(configFileAbsolute);
        if (cfg == null) {
            cfg = new Configuration(configFile);
            cfg.load();
            CONFIGS.put(configFileAbsolute, cfg);
        }

        CONFIGMANAGER$SYNC.invokeExact(cfg, configClass, modId, config.category(), true, (Object) null);

        cfg.save();
    }
}
