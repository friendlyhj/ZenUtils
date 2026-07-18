package youyihj.zenutils.impl.core;

import crafttweaker.CraftTweakerAPI;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;
import zone.rong.mixinbooter.service.ModDiscoverer;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class MixinInit implements IMixinConnector {
    @Override
    public void connect() {
        Mixins.addConfiguration("mixins.zenutils.vanilla.json");
        Mixins.addConfiguration("mixins.zenutils.json");
        if (Configuration.enableRandomTickEvent) {
            Mixins.addConfiguration("mixins.zenutils.randomtickevent.json");
        }
        if (ModDiscoverer.isModPresent("simpledimensions")) {
            Mixins.addConfiguration("mixins.zenutils.simpledimensions.json");
        }
        if (Configuration.enableMixin) {
            try {
                ZenMixin.load();
            } catch (Exception e) {
                CraftTweakerAPI.logError("Failed to load ZenMixin.", e);
            }
        }

    }
}
