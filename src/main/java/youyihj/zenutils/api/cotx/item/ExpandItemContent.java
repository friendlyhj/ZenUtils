package youyihj.zenutils.api.cotx.item;

import com.google.common.collect.Multimap;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.annotation.ExpandContentTweakerEntry;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author youyihj
 */
@ExpandContentTweakerEntry
public class ExpandItemContent extends ItemContent {
    private static final int DEFAULT_MAX_ITEM_USE_DURATION = 32;
    private final ExpandItemRepresentation expandItemRepresentation;

    public ExpandItemContent(ExpandItemRepresentation itemRepresentation) {
        super(itemRepresentation);
        this.expandItemRepresentation = itemRepresentation;
        if (expandItemRepresentation.noRepair) setNoRepair();
        if (!expandItemRepresentation.toolClass.isEmpty()) setFull3D();
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        return Objects.nonNull(expandItemRepresentation.onEntityItemUpdate) && expandItemRepresentation.onEntityItemUpdate.update(CraftTweakerMC.getIEntityItem(entityItem));
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        if (Objects.isNull(expandItemRepresentation.getEntityLifeSpan)) return super.getEntityLifespan(itemStack, world);
        return expandItemRepresentation.getEntityLifeSpan.get(CraftTweakerMC.getIItemStack(itemStack), new MCWorld(world));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ActionResult<ItemStack> actionResult = super.onItemRightClick(world, player, hand);
        if (actionResult.getType() == EnumActionResult.SUCCESS) {
            player.setActiveHand(hand);
        }
        return actionResult;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return (expandItemRepresentation.onItemUseFinish != null && expandItemRepresentation.maxItemUseDuration == 0) ? DEFAULT_MAX_ITEM_USE_DURATION : expandItemRepresentation.maxItemUseDuration;
    }

    @Override
    public int getItemEnchantability() {
        return expandItemRepresentation.enchantability;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        IIngredient repairItem = expandItemRepresentation.repairItem;
        if (repairItem != null) {
            return repairItem.matches(CraftTweakerMC.getIItemStackForMatching(repair));
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        String toolClass = expandItemRepresentation.toolClass;
        if (expandItemRepresentation.destroySpeed == 0.0f || toolClass.isEmpty() || expandItemRepresentation.itemDestroySpeed != null) {
            return super.getDestroySpeed(stack, state);
        }
        if (state.getBlock().isToolEffective(toolClass, state)) {
            return expandItemRepresentation.destroySpeed;
        }
        return 1.0f;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        String toolClass = expandItemRepresentation.toolClass;
        if (toolClass.isEmpty() || expandItemRepresentation.itemDestroyedBlock != null) {
            return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
        }
        if (!world.isRemote && state.getBlockHardness(world, pos) != 0.0f) {
            stack.damageItem(1, entityLiving);
            return true;
        }
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        String toolClass = expandItemRepresentation.toolClass;
        if (!toolClass.isEmpty()) {
            stack.damageItem(toolClass.equals("sword") ? 1 : 2, attacker);
            return true;
        }
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", expandItemRepresentation.attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", expandItemRepresentation.attackSpeed, 0));
        }
        return multimap;
    }

    @Override
    public List<IGeneratedModel> getGeneratedModels() {
        if (!expandItemRepresentation.toolClass.isEmpty()) {
            TemplateFile templateFile = TemplateManager.getTemplateFile(new ResourceLocation(Reference.MODID, "item_tool"));
            Map<String, String> replacements = new HashMap<>();

            replacements.put("texture", Optional.ofNullable(expandItemRepresentation.getTextureLocation())
                    .map(CTResourceLocation::getInternal)
                    .map(ResourceLocation::toString)
                    .orElseGet(() -> "contenttweaker:items/" + expandItemRepresentation.getUnlocalizedName()));
            templateFile.replaceContents(replacements);
            return Collections.singletonList(new GeneratedModel(expandItemRepresentation.getUnlocalizedName(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
        } else {
            return super.getGeneratedModels();
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        boolean result = super.canApplyAtEnchantingTable(stack, enchantment);
        String toolClass = expandItemRepresentation.toolClass;
        if ("sword".equals(toolClass)) {
            result |= enchantment.type == EnumEnchantmentType.WEAPON;
        } else if (!toolClass.isEmpty()) {
            result |= enchantment.type == EnumEnchantmentType.DIGGER;
        }
        return result;
    }

    @ExpandContentTweakerEntry.RepresentationGetter
    public ExpandItemRepresentation getExpandItemRepresentation() {
        return expandItemRepresentation;
    }
}
