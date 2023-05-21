package youyihj.zenutils.impl.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import youyihj.zenutils.api.event.ExpandEventManager;

/**
 * @author youyihj
 */
@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {
    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        ExpandEventManager.handleEntityItemFallEvent((EntityItem) ((Object) this), distance);
        super.fall(distance, damageMultiplier);
    }
}
