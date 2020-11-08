package youyihj.zenutils.cotx;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.mc1120.entity.MCEntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("mods.contenttweaker.Hand")
@ModOnly("contenttweaker")
@SuppressWarnings("unused")
public class ExpandHand {
    @ZenMethod
    @ZenCaster
    public static IEntityEquipmentSlot asEntityEquipmentSlot(Hand hand) {
        switch (hand.getInternal()) {
            case MAIN_HAND:
                return new MCEntityEquipmentSlot(EntityEquipmentSlot.MAINHAND);
            case OFF_HAND:
                return new MCEntityEquipmentSlot(EntityEquipmentSlot.OFFHAND);
            default:
                return null;
        }
    }
}
