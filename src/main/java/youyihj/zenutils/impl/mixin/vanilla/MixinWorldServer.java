package youyihj.zenutils.impl.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import youyihj.zenutils.api.event.RandomTickEvent;

/**
 * @author youyihj
 */
@Mixin(WorldServer.class)
public abstract class MixinWorldServer {
    @WrapOperation(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getTickRandomly()Z"))
    private boolean postRandomTickEvent(
            Block instance, Operation<Boolean> original,
            @Local IBlockState state,
            @Local(index = 14) int x,
            @Local(index = 16) int y,
            @Local(index = 15) int z,
            @Local(index = 6) int chunkX,
            @Local ExtendedBlockStorage extendedBlockStorage,
            @Local(index = 7) int chunkZ
    ) {
        if (MinecraftForge.EVENT_BUS.post(new RandomTickEvent((World) (Object) this, new BlockPos(x + chunkX, y + extendedBlockStorage.getYLocation(), z + chunkZ), state))) {
            return false;
        } else {
            return original.call(instance);
        }
    }
}
