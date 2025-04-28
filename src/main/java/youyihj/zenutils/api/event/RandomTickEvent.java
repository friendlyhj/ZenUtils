package youyihj.zenutils.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author youyihj
 */
@Cancelable
public class RandomTickEvent extends BlockEvent {
    public RandomTickEvent(World world, BlockPos pos, IBlockState state) {
        super(world, pos, state);
    }
}
