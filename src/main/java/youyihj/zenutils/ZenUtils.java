package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.command.ZenCommandRegistrar;
import youyihj.zenutils.processor.HashCheck;
import youyihj.zenutils.util.InternalUtils;
import youyihj.zenutils.util.ZenUtilsGlobal;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.4.1";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker;required-after:redstoneflux";

    private static boolean doHashCheck = false;

    private static Logger logger;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onConstruct(FMLConstructionEvent event) {
        GlobalRegistry.registerGlobal("typeof", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "typeof", Object.class));
        GlobalRegistry.registerGlobal("toString", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "toString", Object.class));
        CraftTweakerAPI.tweaker.getPreprocessorManager().registerPreprocessorAction(HashCheck.PREPROCESSOR_NAME, HashCheck::new);
    }

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public static void onPostInit(FMLPostInitializationEvent event) {
        try {
            hashCheck();
        } catch (Exception e) {
            CraftTweakerAPI.logWarning("fail to hash check, see latest.log");
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStarting(FMLServerStartingEvent event) {
        ZenCommandRegistrar.zenCommandMap.forEach((name, command) -> event.registerServerCommand(command));
    }

    private static void hashCheck() throws Exception {
        if (!doHashCheck) {
            return;
        }
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod.getSource().toString().equals("minecraft.jar")) continue;
            JarFile jarFile = new JarFile(mod.getSource());
            Enumeration<JarEntry> ee = jarFile.entries();
            while (ee.hasMoreElements()) {
                JarEntry entry = ee.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
                    try {
                        if (InternalUtils.isValidHashKey(Class.forName(className))) {
                            CraftTweakerAPI.logDefault(className);
                        }
                    } catch (NoClassDefFoundError | ClassNotFoundException ignored) {

                    }
                }
            }
        }
    }

    public static void enableHashCheck() {
        doHashCheck = true;
    }
}
