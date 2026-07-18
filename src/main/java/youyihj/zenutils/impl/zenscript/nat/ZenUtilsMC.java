package youyihj.zenutils.impl.zenscript.nat;

import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.mc1120.enchantments.MCEnchantmentDefinition;
import crafttweaker.mc1120.entity.MCEntityDefinition;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.EntityEntry;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class ZenUtilsMC {
    @ReflectionInvoked
    public static Enchantment toMCEnchantment(IEnchantmentDefinition definition) {
        return definition == null ? null : (Enchantment) definition.getInternal();
    }

    @ReflectionInvoked
    public static IEnchantmentDefinition toCTEnchantment(Enchantment enchantment) {
        return enchantment == null ? null : new MCEnchantmentDefinition(enchantment);
    }

    @ReflectionInvoked
    public static EntityEntry toMCEntityEntry(IEntityDefinition definition) {
        return definition == null ? null : (EntityEntry) definition.getInternal();
    }

    @ReflectionInvoked
    public static IEntityDefinition toCTEntityDefinition(EntityEntry entry) {
        return entry == null ? null : new MCEntityDefinition(entry);
    }
}
