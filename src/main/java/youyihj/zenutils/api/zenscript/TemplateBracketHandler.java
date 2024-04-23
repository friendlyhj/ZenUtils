package youyihj.zenutils.api.zenscript;

import com.google.common.base.Preconditions;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.brackets.*;
import crafttweaker.zenscript.IBracketHandler;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.TemplateString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@BracketHandler
public class TemplateBracketHandler implements IBracketHandler {
    private final Map<String, IJavaMethod> bracketMethods = new HashMap<>();

    public TemplateBracketHandler() {
        initBracketHandlerMethods();
    }

    protected void initBracketHandlerMethods() {
        registerBracketHandlerMethod("item", CraftTweakerAPI.getJavaMethod(TemplateBracketHandler.class, "getItem", String.class));
        registerBracketHandlerMethod("biome", CraftTweakerAPI.getJavaMethod(BracketHandlerBiome.class, "getBiome", String.class));
        registerBracketHandlerMethod("biomeTypes", CraftTweakerAPI.getJavaMethod(BracketHandlerBiomeType.class, "getBiomeType", String.class));
        registerBracketHandlerMethod("blockstate", CraftTweakerAPI.getJavaMethod(TemplateBracketHandler.class, "getBlockState", String.class));
        registerBracketHandlerMethod("creativetab", CraftTweakerAPI.getJavaMethod(BracketHandlerCreativeTab.class, "getTabFromString", String.class));
        registerBracketHandlerMethod("damagesource", CraftTweakerAPI.getJavaMethod(BracketHandlerDamageSource.class, "getFromString", String.class));
        registerBracketHandlerMethod("enchantment", CraftTweakerAPI.getJavaMethod(BracketHandlerEnchantments.class, "getEnchantment", String.class));
        registerBracketHandlerMethod("entity", CraftTweakerAPI.getJavaMethod(BracketHandlerEntity.class, "getEntity", String.class));
        registerBracketHandlerMethod("liquid", CraftTweakerAPI.getJavaMethod(BracketHandlerLiquid.class, "getLiquid", String.class));
        registerBracketHandlerMethod("fluid", CraftTweakerAPI.getJavaMethod(BracketHandlerLiquid.class, "getLiquid", String.class));
        registerBracketHandlerMethod("ore", CraftTweakerAPI.getJavaMethod(BracketHandlerOre.class, "getOre", String.class));
        registerBracketHandlerMethod("potion", CraftTweakerAPI.getJavaMethod(BracketHandlerPotion.class, "getPotion", String.class));
        registerBracketHandlerMethod("potiontype", CraftTweakerAPI.getJavaMethod(BracketHandlerPotionType.class, "getFromString", String.class));
    }

    protected void registerBracketHandlerMethod(String prefix, IJavaMethod method) {
        Preconditions.checkArgument(Arrays.equals(method.getParameterTypes(), new ZenType[] {ZenType.STRING}));
        bracketMethods.put(prefix, method);
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if (tokens.size() < 3) return null;
        IJavaMethod method = bracketMethods.get(tokens.get(0).getValue().toLowerCase());
        if (method == null || tokens.get(1).getType() != ZenTokener.T_COLON) return null;
        List<Token> idTokens = tokens.subList(2, tokens.size());
        if (idTokens.stream().noneMatch(it -> it.getType() == ZenTokener.T_DOLLAR)) return null;
        return position -> new ExpressionCallStatic(position, environment, method, TemplateString.getExpression(idTokens, position, environment));
    }

    @Override
    public String getRegexMatchingString() {
        return bracketMethods.keySet().stream().collect(Collectors.joining("|", "(", ").*"));
    }

    @ReflectionInvoked
    public static IItemStack getItem(String name) {
        String[] split = name.split(":");
        if (split.length == 2) {
            return BracketHandlerItem.getItem(name, 0);
        } else {
            return BracketHandlerItem.getItem(
                    name.substring(0, name.lastIndexOf(':')),
                    split[2].equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(split[2])
            );
        }
    }

    @ReflectionInvoked
    public static IBlockState getBlockState(String expression) {
        String[] split = expression.split(":", 3);
        return BracketHandlerBlockState.getBlockState(split[0] + ":" + split[1], split.length == 3 ? split[2] : null);
    }
}
