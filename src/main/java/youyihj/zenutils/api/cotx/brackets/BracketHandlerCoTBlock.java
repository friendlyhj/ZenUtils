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
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import youyihj.zenutils.api.annotation.ExpandCoTEntry;
import youyihj.zenutils.impl.util.ReflectUtils;
import youyihj.zenutils.impl.zenscript.ExpressionCallStaticThenCastWithStringArg;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@BracketHandler(priority = 100)
public class BracketHandlerCoTBlock implements IBracketHandler {
    private static final IJavaMethod representationGetter = CraftTweakerAPI.getJavaMethod(BracketHandlerCoTBlock.class, "getBlockRepresentation", String.class);

    public static BlockRepresentation getBlockRepresentation(String name) {
        Block block = getBlock(name);
        if (block instanceof BlockContent) {
            try {
                if (block.getClass() == BlockContent.class) {
                    return ((BlockRepresentation) ReflectUtils.removePrivate(BlockContent.class, "blockRepresentation").get(block));
                } else if (block.getClass().isAnnotationPresent(ExpandCoTEntry.class)) {
                    for (Method method : block.getClass().getMethods()) {
                        if (method.isAnnotationPresent(ExpandCoTEntry.RepresentationGetter.class)) {
                            return ((BlockRepresentation) method.invoke(block));
                        }
                    }
                }
            } catch (ReflectiveOperationException | ClassCastException e) {
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
        BlockRepresentation blockRepresentation = getBlockRepresentation(name);
        if (blockRepresentation == null) {
            return null;
        }
        return position -> new ExpressionCallStaticThenCastWithStringArg(position, environment, representationGetter, blockRepresentation.getClass(), name);
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
