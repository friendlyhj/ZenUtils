package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.game.IGame;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionType;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBiomeType;
import crafttweaker.mc1120.brackets.*;
import net.minecraft.potion.PotionType;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Provides missing bracket handler methods accept a string value
 * <p>
 * For items, {@link crafttweaker.api.item.IItemUtils}. For liquids, {@link IGame#getLiquid(String)}
 * For Entities, {@link IGame#getEntity(String)}. For Blocks, {@link crafttweaker.mc1120.block.expand.ExpandBlockState#getBlockState(String, String...)}
 *
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.game.IGame")
public class ExpandGame {
    @ZenMethod
    public static IPotion getPotion(IGame game, String name) {
        return BracketHandlerPotion.getPotion(name);
    }

    @ZenMethod
    public static IBiome getBiome(IGame game, String name) {
        return BracketHandlerBiome.getBiome(name);
    }

    @ZenMethod
    public static IBiomeType getBiomeType(IGame game, String name) {
        return BracketHandlerBiomeType.getBiomeType(name);
    }

    @ZenMethod
    public static ICreativeTab getCreativeTab(IGame game, String name) {
        return CraftTweakerMC.creativeTabs.get(name);
    }

    @ZenMethod
    public static IEnchantmentDefinition getEnchantment(IGame game, String name) {
        return BracketHandlerEnchantments.enchantments.get(name);
    }

    @ZenMethod
    public static IPotionType getPotionType(IGame game, String name) {
        return CraftTweakerMC.getIPotionType(PotionType.getPotionTypeForName(name));
    }

    @ZenMethod
    public static IOreDictEntry getOreDictEntry(IGame game, String name) {
        return BracketHandlerOre.getOre(name);
    }
}
