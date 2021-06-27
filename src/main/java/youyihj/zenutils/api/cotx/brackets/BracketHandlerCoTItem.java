package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import youyihj.zenutils.api.cotx.item.ExpandItemContent;
import youyihj.zenutils.api.cotx.item.ExpandItemRepresentation;
import youyihj.zenutils.impl.util.InstanceOfResult;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@BracketHandler(priority = 100)
public class BracketHandlerCoTItem implements IBracketHandler {

    private final IJavaMethod methodToGetCoTItem;
    private final IJavaMethod methodToGetExpandItem;

    public BracketHandlerCoTItem() {
        this.methodToGetCoTItem = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTItem.class, "getCoTItem", String.class);
        this.methodToGetExpandItem = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTItem.class, "getExpandItem", String.class);
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if(tokens.size() > 2) {
            if(tokens.get(0).getValue().equals("cotItem") && tokens.get(1).getValue().equals(":")) {
                return find(environment, tokens);
            }
        }
        return null;
    }

    public static ItemRepresentation getCoTItem(String name) {
        Item item = getItem(name);
        if (item instanceof ItemContent) {
            try {
                return ((ItemRepresentation) ReflectUtils.removePrivate(ItemContent.class, "itemRepresentation").get(item));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                CraftTweakerAPI.logError(null, e);
            }
        }
        return null;
    }

    public static ExpandItemRepresentation getExpandItem(String name) {
        Item item = getItem(name);
        if (item instanceof ExpandItemContent) {
            return ((ExpandItemContent) item).getExpandItemRepresentation();
        }
        return null;
    }

    private static Item getItem(String name) {
        Item item = ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM")
                .get(new ResourceLocation(ContentTweaker.MOD_ID, name));
        if (item instanceof ItemContent) {
            LateGetContentLookup.addItem(((ItemContent) item));
        }
        return item;
    }

    private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens) {
        String name = tokens.get(2).getValue();
        IJavaMethod method;
        switch (InstanceOfResult.find(ExpandItemContent.class, ItemContent.class, getItem(name))) {
            case A:
                method = methodToGetExpandItem;
                break;
            case B:
                method = methodToGetCoTItem;
                break;
            default:
                return null;
        }
        return position -> new ExpressionCallStatic(position, environment, method, new ExpressionString(position, name));
    }

    @Override
    public String getRegexMatchingString() {
        return "cotItem:.*";
    }

    @Override
    public Class<?> getReturnedClass() {
        return ItemRepresentation.class;
    }
}
