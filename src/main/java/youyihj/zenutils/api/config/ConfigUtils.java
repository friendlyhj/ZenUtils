package youyihj.zenutils.api.config;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.SystemUtils;
import stanhebben.zenscript.value.IAny;
import youyihj.zenutils.api.config.elements.ConfigGroup;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@ZenRegister
@ZenClass("mods.zenutils.config.ConfigUtils")
public class ConfigUtils {

    @ZenMethod
    public static ConfigGroup named(String name) {
        return new ConfigGroup(null, name);
    }

    @ZenMethod // Map<String, ?> dataMap
    public static DataMap dataMap(Map<String, Object> dataMap) {
        HashMap<String, IData> actualMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            actualMap.put(entry.getKey(), castToData(entry.getValue()));
        }
        return new DataMap(actualMap, false);
    }

    public static IData castToData(Object o) {
        if (o instanceof Integer) {
            return new DataInt((Integer)o);
        } else if (o instanceof Double) {
            return new DataDouble((Double)o);
        } else if (o instanceof Boolean) {
            return new DataBool((Boolean)o);
        } else if (o instanceof String) {
            return new DataString((String) o);
        } else if (o.getClass().isArray()) {
            return castToDataList(cast(o));
        } else return null;
    }

    public static DataList castToDataList(Object[] o) {
        LinkedList<IData> data = new LinkedList<>();
        for (Object obj : o) {
            data.add(castToData(obj));
        }
        return new DataList(data, true);
    }

    @SuppressWarnings("unchecked")
    public static<T> T cast(Object o) {
        return (T)o;
    }

    public static class ClassProvider implements IClassTransformer {
        public static final Map<String, byte[]> classes = new HashMap<>();

        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass) {
            return classes.getOrDefault(transformedName, basicClass);
        }
    }

    /**
     * MIT License
     *
     * Copyright (c) 2022 CleanroomMC
     *
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     *
     * The above copyright notice and this permission notice shall be included in all
     * copies or substantial portions of the Software.
     *
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     * SOFTWARE.
     */
    public static class ConfigAnytimeAnytime {
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
}
