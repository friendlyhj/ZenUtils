package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * @author youyihj
 */
public class ExpandItemContent extends ItemContent {
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
}
