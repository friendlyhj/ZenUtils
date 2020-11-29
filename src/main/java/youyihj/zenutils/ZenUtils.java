package youyihj.zenutils;

import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import youyihj.zenutils.command.ZenCommandRegistrar;
import youyihj.zenutils.util.ZenUtilsGlobal;

/**
 * @author youyihj
 */
@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.4.1";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker;required-after:redstoneflux";

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onConstruct(FMLConstructionEvent event) {
        GlobalRegistry.registerGlobal("typeof", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "typeof", Object.class));
        GlobalRegistry.registerGlobal("toString", GlobalRegistry.getStaticFunction(ZenUtilsGlobal.class, "toString", Object.class));
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStarting(FMLServerStartingEvent event) {
        ZenCommandRegistrar.zenCommandMap.forEach((name, command) -> event.registerServerCommand(command));
    }
}
