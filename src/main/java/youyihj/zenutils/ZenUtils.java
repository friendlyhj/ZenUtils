package youyihj.zenutils;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import youyihj.zenutils.capability.ZenWorldCapabilityHandler;
import youyihj.zenutils.command.ZenCommandRegistrar;
import youyihj.zenutils.logger.ZenUtilsLogger;
import youyihj.zenutils.preprocessor.SuppressErrorPreprocessor;
import youyihj.zenutils.util.InternalUtils;
import youyihj.zenutils.util.ReflectUtils;
import youyihj.zenutils.util.ZenUtilsGlobal;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.6.0";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker;required-after:redstoneflux";

    @Mod.EventHandler
    public static void onConstruct(FMLConstructionEvent event) {
        InternalUtils.checkCrTVersion();
        GlobalRegistry.registerGlobal("typeof", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "typeof", Object.class));
        GlobalRegistry.registerGlobal("toString", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "toString", Object.class));
        CraftTweakerAPI.tweaker.getPreprocessorManager().registerPreprocessorAction(SuppressErrorPreprocessor.NAME, SuppressErrorPreprocessor::new);
        try {
            final Field loggerField = ReflectUtils.removePrivateFinal(CrafttweakerImplementationAPI.class, "logger");
            loggerField.set(null, new ZenUtilsLogger(CrafttweakerImplementationAPI.logger));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            CraftTweakerAPI.logInfo("Fail to set crafttweaker logger to zenutils one. #suppress preprocessor cannot work properly.");
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        ZenWorldCapabilityHandler.register();
    }

    @Mod.EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        ZenCommandRegistrar.zenCommandMap.forEach((name, command) -> event.registerServerCommand(command));
    }
}
