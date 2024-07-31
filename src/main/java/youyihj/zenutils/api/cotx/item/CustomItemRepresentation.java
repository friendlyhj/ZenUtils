package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import net.minecraft.item.Item;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.CustomItem")
public class CustomItemRepresentation {
    private Item mcItem;
    private final CustomItem customItem;
    private final String name;

    public CustomItemRepresentation(String name, CustomItem customItem) {
        this.name = name;
        this.customItem = customItem;
    }

    @ZenMethod
    public void register() {
        try {
            // TODO: find constructor
            mcItem = (Item) customItem.define().newInstance();
            mcItem.setTranslationKey(name);
            ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(mcItem);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Item getMcItem() {
        return mcItem;
    }
}
