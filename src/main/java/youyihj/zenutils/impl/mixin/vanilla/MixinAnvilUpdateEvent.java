package youyihj.zenutils.impl.mixin.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import youyihj.zenutils.impl.mixin.itf.IAnvilUpdateEventExtension;

/**
 * @author youyihj
 */
@Mixin(value = AnvilUpdateEvent.class, remap = false)
public abstract class MixinAnvilUpdateEvent implements IAnvilUpdateEventExtension {
    @Unique
    private EntityPlayer zu$player;

    @Override
    public EntityPlayer zu$getPlayer() {
        return zu$player;
    }

    @Override
    public void zu$setPlayer(EntityPlayer player) {
        this.zu$player = player;
    }
}
