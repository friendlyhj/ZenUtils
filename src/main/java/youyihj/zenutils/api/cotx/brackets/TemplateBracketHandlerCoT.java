package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.modules.materials.brackethandler.MaterialPartBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.BlockBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.ItemBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.LiquidBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.creativetab.CreativeTabBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.materials.MaterialBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.SoundEventBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.SoundTypeBracketHandler;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.api.item.IItemStack;
import net.minecraftforge.oredict.OreDictionary;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.api.zenscript.SidedZenRegister;
import youyihj.zenutils.api.zenscript.TemplateBracketHandler;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = ZenUtils.MOD_COT)
@BracketHandler(priority = 9)
public class TemplateBracketHandlerCoT extends TemplateBracketHandler {
    @Override
    protected void initBracketHandlerMethods() {
        registerBracketHandlerMethod("creativeTab", CraftTweakerAPI.getJavaMethod(CreativeTabBracketHandler.class, "getCreativeTab", String.class));
        registerBracketHandlerMethod("item", CraftTweakerAPI.getJavaMethod(TemplateBracketHandlerCoT.class, "getItem", String.class));
        registerBracketHandlerMethod("block", CraftTweakerAPI.getJavaMethod(TemplateBracketHandlerCoT.class, "getBlock", String.class));
        registerBracketHandlerMethod("blockmaterial", CraftTweakerAPI.getJavaMethod(MaterialBracketHandler.class, "getBlockMaterial", String.class));
        registerBracketHandlerMethod("soundevent", CraftTweakerAPI.getJavaMethod(SoundEventBracketHandler.class, "getSoundEvent", String.class));
        registerBracketHandlerMethod("soundetype", CraftTweakerAPI.getJavaMethod(SoundTypeBracketHandler.class, "getSoundType", String.class));
        registerBracketHandlerMethod("liquid", CraftTweakerAPI.getJavaMethod(LiquidBracketHandler.class, "getFromString", String.class));
        registerBracketHandlerMethod("fluid", CraftTweakerAPI.getJavaMethod(LiquidBracketHandler.class, "getFromString", String.class));
        registerBracketHandlerMethod("materialpart", CraftTweakerAPI.getJavaMethod(MaterialPartBracketHandler.class, "getMaterialPart", String.class));
    }

    @ReflectionInvoked
    public static IItemStack getItem(String expression) {
        String[] split = expression.split(":");
        if (split.length == 2) {
            return ItemBracketHandler.getItem(expression, 0);
        } else {
            return ItemBracketHandler.getItem(
                    expression.substring(0, expression.lastIndexOf(':')),
                    split[2].equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(split[2])
            );
        }
    }

    @ReflectionInvoked
    public static ICTBlockState getBlock(String expression) {
        String[] split = expression.split(":");
        if (split.length == 2) {
            return BlockBracketHandler.getBlockState(expression, 0);
        } else {
            return BlockBracketHandler.getBlockState(
                    expression.substring(0, expression.lastIndexOf(':')),
                    Integer.parseInt(split[2])
            );
        }
    }
}
