package youyihj.zenutils.impl.config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ConfigChangedHandler {
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(ConfigAnytimeAnytime.registeredGuiContainers.containsKey(event.getModID())) {
            ConfigAnytimeAnytime.registeredGuiSaveMethods.get(event.getModID()).run();
        }
    }
}
