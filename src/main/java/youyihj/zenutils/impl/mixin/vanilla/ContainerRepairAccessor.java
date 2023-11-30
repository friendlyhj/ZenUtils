package youyihj.zenutils.impl.mixin.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author youyihj
 */
@Mixin(ContainerRepair.class)
public interface ContainerRepairAccessor {
    @Accessor
    EntityPlayer getPlayer();
}
