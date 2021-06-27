package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import youyihj.zenutils.api.cotx.block.ExpandBlockContent;
import youyihj.zenutils.api.cotx.block.ExpandBlockRepresentation;
import youyihj.zenutils.impl.util.InstanceOfResult;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@BracketHandler(priority = 100)
public class BracketHandlerCoTBlock implements IBracketHandler {
    private final IJavaMethod methodToGetCoTBlock;
    private final IJavaMethod methodToGetExpandBlock;

    public BracketHandlerCoTBlock() {
        this.methodToGetCoTBlock = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTBlock.class, "getCoTBlock", String.class);
        this.methodToGetExpandBlock = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTBlock.class, "getExpandBlock", String.class);
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if(tokens.size() > 2) {
            if(tokens.get(0).getValue().equals("cotBlock") && tokens.get(1).getValue().equals(":")) {
                return find(environment, tokens);
            }
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

    public static ExpandBlockRepresentation getExpandBlock(String name) {
        Block block = getBlock(name);
        if (block instanceof ExpandBlockContent) {
            return ((ExpandBlockContent) block).getExpandBlockRepresentation();
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

    private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens) {
        String name = tokens.get(2).getValue();
        IJavaMethod method;
        switch (InstanceOfResult.find(ExpandBlockContent.class, BlockContent.class, getBlock(name))) {
            case A:
                method = methodToGetExpandBlock;
                break;
            case B:
                method = methodToGetCoTBlock;
                break;
            default:
                return null;
        }
        return position -> new ExpressionCallStatic(position, environment, method, new ExpressionString(position, name));
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
