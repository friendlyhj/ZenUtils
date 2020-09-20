package youyihj.zenutils.cotx.item;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.cotx.EnergyItem")
public class EnergyItemRepresentation extends ExpandItemRepresentation {

    int capacity;
    int maxReceive;
    int maxExtract;

    public EnergyItemRepresentation(String unlocalizedName, int capacity, int maxReceive, int maxExtract) {
        super(unlocalizedName);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(new EnergyItemContent(this));
    }
}
