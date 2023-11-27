package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;
import crafttweaker.api.item.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.api.cotx.function.IEntityItemUpdate;
import youyihj.zenutils.api.cotx.function.IGetEntityLifeSpan;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.cotx.Item")
public class ExpandItemRepresentation extends ItemRepresentation {

    public ExpandItemRepresentation(String unlocalizedName) {
        setUnlocalizedName(unlocalizedName);
    }

    @ZenProperty
    public IEntityItemUpdate onEntityItemUpdate = null;

    @ZenProperty
    public boolean noRepair = false;

    @ZenProperty
    public IGetEntityLifeSpan getEntityLifeSpan = null;

    @ZenProperty
    public int maxItemUseDuration = 0;

    @ZenProperty
    public IIngredient repairItem;

    @ZenProperty
    public float attackSpeed;

    @ZenProperty
    public float attackDamage;

    @ZenProperty
    public float destroySpeed;

    @ZenProperty
    public int enchantability;

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(new ExpandItemContent(this));
    }
}
