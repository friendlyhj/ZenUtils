package youyihj.zenutils.api.cotx.item;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import net.minecraftforge.fml.common.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = ZenUtils.MOD_COT)
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
    @Optional.Method(modid = "redstoneflux")
    public void register() {
        ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(new EnergyItemContent(this));
    }
}
