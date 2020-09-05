package youyihj.zenutils;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import youyihj.zenutils.item.EnergyItemRepresentation;

@ZenRegister
@ModOnly("contenttweaker")
@ZenExpansion("mods.contenttweaker.VanillaFactory")
public class ExpandVanillaFactory {
    @ZenMethodStatic
    public static EnergyItemRepresentation createEnergyItem(String unlocalizedName, int capacity, int maxReceive, int maxExtract) {
        return new EnergyItemRepresentation(unlocalizedName, capacity, maxReceive, maxExtract);
    }
}
