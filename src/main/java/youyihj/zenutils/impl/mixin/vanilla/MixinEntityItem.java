package youyihj.zenutils.impl.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;setDead()V"))
    private void fireAttackedEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ExpandEventManager.handleEntityItemDeathEvent((EntityItem) ((Object) this), source);
    }
}
