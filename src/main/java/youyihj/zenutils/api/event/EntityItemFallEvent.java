package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.event.EntityItemFallEvent")
public class EntityItemFallEvent {
    private final EntityItem entityItem;
    private final float distance;

    public EntityItemFallEvent(EntityItem entityItem, float distance) {
        this.entityItem = entityItem;
        this.distance = distance;
    }

    @ZenGetter("item")
    public IEntityItem getItem() {
        return CraftTweakerMC.getIEntityItem(entityItem);
    }

    @ZenGetter("distance")
    public float getDistance() {
        return distance;
    }

    @ZenGetter("blockState")
    public IBlockState getBlockState() {
        World world = entityItem.world;
        List<AxisAlignedBB> aabbList = world.getCollisionBoxes(entityItem, entityItem.getEntityBoundingBox().offset(0, -0.2, 0));
        if (!aabbList.isEmpty()) {
            AxisAlignedBB aabb = aabbList.get(0);
            return CraftTweakerMC.getBlockState(world.getBlockState(new BlockPos(MathHelper.floor(aabb.minX), MathHelper.floor(aabb.minY), MathHelper.floor(aabb.minZ))));
        }
        return CraftTweakerMC.getBlockState(Blocks.AIR.getDefaultState());
    }
}
