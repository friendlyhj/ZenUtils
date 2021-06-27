package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author youyihj
 */
public class ExpandItemContent extends ItemContent {
    private static final int DEFAULT_MAX_ITEM_USE_DURATION = 32;
    private final ExpandItemRepresentation expandItemRepresentation;

    public ExpandItemContent(ExpandItemRepresentation itemRepresentation) {
        super(itemRepresentation);
        this.expandItemRepresentation = itemRepresentation;
        if (expandItemRepresentation.noRepair) setNoRepair();
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        return Objects.nonNull(expandItemRepresentation.onEntityItemUpdate) && expandItemRepresentation.onEntityItemUpdate.update(CraftTweakerMC.getIEntityItem(entityItem));
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        if (Objects.isNull(expandItemRepresentation.getEntityLifeSpan)) return super.getEntityLifespan(itemStack, world);
        return expandItemRepresentation.getEntityLifeSpan.get(CraftTweakerMC.getIItemStack(itemStack), new MCWorld(world));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ActionResult<ItemStack> actionResult = super.onItemRightClick(world, player, hand);
        if (actionResult.getType() == EnumActionResult.SUCCESS) {
            player.setActiveHand(hand);
        }
        return actionResult;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return (expandItemRepresentation.onItemUseFinish != null && expandItemRepresentation.maxItemUseDuration == 0) ? DEFAULT_MAX_ITEM_USE_DURATION : expandItemRepresentation.maxItemUseDuration;

    }

    public ExpandItemRepresentation getExpandItemRepresentation() {
        return expandItemRepresentation;
    }
}
