package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.IEntityEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.event.EntityItemFallEvent")
public class EntityItemFallEvent implements IEntityEvent {
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

    @ZenGetter("blockStates")
    public List<IBlockState> getBlockStates() {
        World world = entityItem.world;
        List<AxisAlignedBB> aabbList = new ArrayList<>();
        world.getCollisionBoxes(entityItem, entityItem.getEntityBoundingBox().offset(0, -0.2, 0), true, aabbList);
        return aabbList.stream()
                .map(it -> world.getBlockState(new BlockPos(MathHelper.floor(it.minX), MathHelper.floor(it.minY), MathHelper.floor(it.minZ))))
                .map(CraftTweakerMC::getBlockState)
                .collect(Collectors.toList());
    }

    @Override
    public IEntity getEntity() {
        return getItem();
    }
}
