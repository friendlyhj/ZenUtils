package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import net.minecraft.potion.Potion;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethodStatic;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.potions.IPotion")
public class ExpandPotion {
    @ZenGetter("numericalId")
    public static int getNumericalId(IPotion potion) {
        return Potion.getIdFromPotion(CraftTweakerMC.getPotion(potion));
    }

    @ZenMethodStatic
    public static IPotion getFromNumericalId(int id) {
        return CraftTweakerMC.getIPotion(Potion.getPotionById(id));
    }
}
