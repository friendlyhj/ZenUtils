package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.events.handling.MCPlayerAnvilUpdateEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.spongepowered.asm.mixin.*;
import youyihj.zenutils.impl.mixin.itf.IAnvilUpdateEventExtension;

/**
 * @author youyihj
 */
@Mixin(value = MCPlayerAnvilUpdateEvent.class, remap = false)
@Implements(@Interface(iface = IPlayerEvent.class, prefix = "zu$"))
public abstract class MixinMCPlayerAnvilUpdateEvent {
    @Shadow
    @Final
    private AnvilUpdateEvent event;

    public IPlayer zu$getPlayer() {
        return CraftTweakerMC.getIPlayer(((IAnvilUpdateEventExtension) event).zu$getPlayer());
    }
}
