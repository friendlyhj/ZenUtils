package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
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
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import youyihj.zenutils.api.cotx.block.ExpandBlockContent;
import youyihj.zenutils.api.cotx.block.ExpandBlockRepresentation;
import youyihj.zenutils.impl.util.ReflectUtils;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@BracketHandler(priority = 100)
public class BracketHandlerCoTBlock implements IBracketHandler {

    private final IJavaMethod methodToGetCoTBlock;
    private static final Map<Class<? extends BlockContent>, IJavaMethod> methodToGetExpandBlockList = new HashMap<>();

    public BracketHandlerCoTBlock() {
        this.methodToGetCoTBlock = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTBlock.class, "getCoTBlock", String.class);
        addMethodToGetExpandBlock(ExpandBlockContent.class, BracketHandlerCoTBlock.class, "getExpandBlock", String.class);
    }

    public static ExpandBlockRepresentation getExpandBlock(String name) {
        try {
            return getExpandBlock(ExpandBlockContent.class, name);
        } catch (ReflectiveOperationException e) {
            CraftTweakerAPI.logError(null, e);
        }
        return null;
    }

    public static BlockRepresentation getCoTBlock(String name) {
        Block block = getBlock(name);
        if (block instanceof BlockContent) {
            try {
                return ((BlockRepresentation) ReflectUtils.removePrivate(BlockContent.class, "blockRepresentation").get(block));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                CraftTweakerAPI.logError(null, e);
            }
        }
        return null;
    }

    private static Block getBlock(String name) {
        Block block = ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK")
            .get(new ResourceLocation(ContentTweaker.MOD_ID, name));
        if (block instanceof BlockContent) {
            LateGetContentLookup.addBlock(((BlockContent) block));
        }
        return block;
    }

    public static <T extends BlockRepresentation> T getExpandBlock(Class<? extends BlockContent> tClass, String name) throws ReflectiveOperationException {
        Block block = getBlock(name);
        if (tClass.isInstance(block)) {
            //noinspection unchecked
            return (T) block.getClass().getMethod("getRepresentation").invoke(block);
        }
        return null;
    }

    public static void addMethodToGetExpandBlock(Class<? extends BlockContent> c, Class<?> cls, String name, Class<?>... parameterTypes) {
        methodToGetExpandBlockList.put(c, JavaMethod.get(GlobalRegistry.getTypes(), cls, name, parameterTypes));
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if (tokens.size() > 2) {
            if (tokens.get(0).getValue().equals("cotBlock") && tokens.get(1).getValue().equals(":")) {
                return find(environment, tokens);
            }
        }
        return null;
    }

    private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens) {
        String name = tokens.get(2).getValue();
        Block block = getBlock(name);

        for (Entry<Class<? extends BlockContent>, IJavaMethod> entry : methodToGetExpandBlockList.entrySet()) {
            if (entry.getKey().isInstance(block)) {
                return position -> new ExpressionCallStatic(position, environment, entry.getValue(), new ExpressionString(position, name));
            }
        }

        if (block instanceof BlockContent) {
            return position -> new ExpressionCallStatic(position, environment, methodToGetCoTBlock, new ExpressionString(position, name));
        }
        return null;
    }

    @Override
    public String getRegexMatchingString() {
        return "cotBlock:.*";
    }

    @Override
    public Class<?> getReturnedClass() {
        return BlockRepresentation.class;
    }
}
