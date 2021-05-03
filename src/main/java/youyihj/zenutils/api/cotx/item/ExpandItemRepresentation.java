package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.api.cotx.function.IEntityItemUpdate;
import youyihj.zenutils.api.cotx.function.IGetEntityLifeSpan;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
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

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(new ExpandItemContent(this ));
    }
}
