package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.event.PlayerAnvilUpdateEvent;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author youyihj
 */
@Mixin(PlayerAnvilUpdateEvent.class)
@Implements(@Interface(iface = IPlayerEvent.class, prefix = "zu$"))
public interface MixinPlayerAnvilUpdateEvent {
}
