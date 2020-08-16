package youyihj.zenutils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import youyihj.zenutils.command.ZenCommandRegistrar;

@Mod(modid = ZenUtils.MODID, name = ZenUtils.NAME, version = ZenUtils.VERSION, dependencies = ZenUtils.DEPENDENCIES)
public class ZenUtils {
    public static final String MODID = "zenutils";
    public static final String NAME = "ZenUtils";
    public static final String VERSION = "1.1.3";
    public static final String DEPENDENCIES = "required-after:crafttweaker;after:contenttweaker";


    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStarting(FMLServerStartingEvent event) {
        ZenCommandRegistrar.zenCommandMap.forEach((name, command) -> event.registerServerCommand(command));
    }
}
