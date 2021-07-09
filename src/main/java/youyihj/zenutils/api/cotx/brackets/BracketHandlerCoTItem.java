package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import youyihj.zenutils.api.cotx.item.ExpandItemContent;
import youyihj.zenutils.api.cotx.item.ExpandItemRepresentation;
import youyihj.zenutils.impl.util.ReflectUtils;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@BracketHandler(priority = 100)
public class BracketHandlerCoTItem implements IBracketHandler {

    private final IJavaMethod methodToGetCoTItem;
    private static final Map<Class<? extends ItemContent>, IJavaMethod> methodToGetExpandItemList = new HashMap<>();

    public BracketHandlerCoTItem() {
        this.methodToGetCoTItem = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTItem.class, "getCoTItem", String.class);
        addMethodToGetExpandItem(ExpandItemContent.class, BracketHandlerCoTItem.class, "getExpandItem", String.class);
    }

    public static ExpandItemRepresentation getExpandItem(String name) {
        try {
            return getExpandItem(ExpandItemContent.class, name);
        } catch (ReflectiveOperationException e) {
            CraftTweakerAPI.logError(null, e);
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

    public static <T extends ItemRepresentation> T getExpandItem(Class<? extends ItemContent> tClass, String name) throws ReflectiveOperationException {
        Item item = getItem(name);
        if (tClass.isInstance(item)) {
            //noinspection unchecked
            return (T) item.getClass().getMethod("getRepresentation").invoke(item);
        }
        return null;
    }

    public static void addMethodToGetExpandItem(Class<? extends ItemContent> c, Class<?> cls, String name, Class<?>... parameterTypes) {
        methodToGetExpandItemList.put(c, JavaMethod.get(GlobalRegistry.getTypes(), cls, name, parameterTypes));
    }

    public static Item getItem(String name) {
        Item item = ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").get(new ResourceLocation(ContentTweaker.MOD_ID, name));
        if (item instanceof ItemContent) {
            LateGetContentLookup.addItem(((ItemContent) item));
        }
        return item;
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if (tokens.size() > 2) {
            if (tokens.get(0).getValue().equals("cotItem") && tokens.get(1).getValue().equals(":")) {
                return find(environment, tokens);
            }
        }
        return null;
    }

    private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens) {
        String name = tokens.get(2).getValue();
        Item item = getItem(name);

        for (Entry<Class<? extends ItemContent>, IJavaMethod> entry : methodToGetExpandItemList.entrySet()) {
            if (entry.getKey().isInstance(item)) {
                return position -> new ExpressionCallStatic(position, environment, entry.getValue(), new ExpressionString(position, name));
            }
        }
        if (item instanceof ItemContent) {
            return position -> new ExpressionCallStatic(position, environment, methodToGetCoTItem, new ExpressionString(position, name));
        }
        return null;
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
