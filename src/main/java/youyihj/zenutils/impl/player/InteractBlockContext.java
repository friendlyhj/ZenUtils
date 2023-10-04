package youyihj.zenutils.impl.player;

import com.google.common.base.Suppliers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
public class InteractBlockContext {
    private final Supplier<RayTraceResult> rayTraceResult;
    private BlockPos pos;
    private EnumFacing side;
    private float hitX, hitY, hitZ;

    public InteractBlockContext(EntityPlayer player, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        double reachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        this.rayTraceResult = Suppliers.memoize(() -> ForgeHooks.rayTraceEyes(player, reachDistance));
        this.pos = pos;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public InteractBlockContext(EntityPlayer player, BlockPos pos, EnumFacing side) {
        this(player, pos, side, -1, -1, -1);
    }

    public BlockPos getPos() {
        if (pos == null) {
            if (rayTraceResult.get().typeOfHit == RayTraceResult.Type.MISS) {
                throw new MissingParameterException("Player doesn't target at any block, please define position explicitly");
            }
            pos = rayTraceResult.get().getBlockPos();
        }
        return pos;
    }

    public EnumFacing getSide() {
        if (side == null) {
            if (!Objects.equals(getPos(), rayTraceResult.get().getBlockPos())) {
                throw new MissingParameterException("Player doesn't target at the given position, please define facing explicitly");
            }
            side = rayTraceResult.get().sideHit;
        }
        return side;
    }

    public float getHitX() {
        if (hitX == -1) {
            if (!Objects.equals(getPos(), rayTraceResult.get().getBlockPos())) {
                throw new MissingParameterException("Player doesn't target at the given position, please define hitX explicitly");
            }
            hitX = (float) (rayTraceResult.get().hitVec.x - getPos().getX());
        }
        return hitX;
    }

    public float getHitY() {
        if (hitY == -1) {
            if (!Objects.equals(getPos(), rayTraceResult.get().getBlockPos())) {
                throw new MissingParameterException("Player doesn't target at the given position, please define hitY explicitly");
            }
            hitY = (float) (rayTraceResult.get().hitVec.y - getPos().getY());
        }
        return hitY;
    }

    public float getHitZ() {
        if (hitZ == -1) {
            if (!Objects.equals(getPos(), rayTraceResult.get().getBlockPos())) {
                throw new MissingParameterException("Player doesn't target at the given position, please define hitZ explicitly");
            }
            hitZ = (float) (rayTraceResult.get().hitVec.z - getPos().getZ());
        }
        return hitZ;
    }

    public static class MissingParameterException extends RuntimeException {
        public MissingParameterException(String message) {
            super(message);
        }
    }
}
