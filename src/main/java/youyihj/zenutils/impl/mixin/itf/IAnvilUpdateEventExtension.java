package youyihj.zenutils.impl.mixin.itf;

import net.minecraft.entity.player.EntityPlayer;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
public interface IAnvilUpdateEventExtension {
    @ReflectionInvoked
    EntityPlayer zu$getPlayer();

    void zu$setPlayer(EntityPlayer player);
}
