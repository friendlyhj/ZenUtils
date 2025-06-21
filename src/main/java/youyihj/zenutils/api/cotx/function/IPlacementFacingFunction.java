package youyihj.zenutils.api.cotx.function;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IVector3d;
import net.minecraft.util.EnumFacing;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.block.DirectionalBlockRepresentation;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.IPlacementFacingFunction")
@FunctionalInterface
public interface IPlacementFacingFunction {
    @ZenMethod
    Facing apply(IWorld world, IBlockPos pos, Facing facing, float hitX, float hitY, float hitZ, int meta, IEntityLivingBase placer, Hand hand);

    @ZenMethod
    static IPlacementFacingFunction placer(DirectionalBlockRepresentation blockRepresentation) {
        return fromPlacer(blockRepresentation.getDirections());
    }

    @ZenMethod
    static IPlacementFacingFunction side() {
        return (world, pos, facing, hitX, hitY, hitZ, meta, placer, hand) -> facing;
    }

    static IPlacementFacingFunction fromPlacer(DirectionalBlockRepresentation.Directions directions) {
        return (world, pos, facing, hitX, hitY, hitZ, meta, placer, hand) -> {
            IVector3d lookVec = placer.getLookingDirection();
            switch (directions) {
                case ALL:
                    return Facing.of(EnumFacing.getFacingFromVector((float) lookVec.getX(), (float) lookVec.getY(), (float) lookVec.getZ()));
                case HORIZONTAL:
                    return Facing.of(CraftTweakerMC.getEntity(placer).getHorizontalFacing());
                case VERTICAL:
                    return lookVec.getY() > 0.0 ? Facing.up() : Facing.down();
                default:
                    throw new IllegalStateException("Unknown Direction: " + directions);
            }
        };
    }
}
