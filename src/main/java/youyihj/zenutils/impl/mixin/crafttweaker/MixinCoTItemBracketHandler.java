package youyihj.zenutils.impl.mixin.crafttweaker;

import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.ItemBracketHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author youyihj
 */
@Pseudo
@Mixin(value = ItemBracketHandler.class, remap = false)
public abstract class MixinCoTItemBracketHandler {
    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Ljava/lang/Integer;parseInt(Ljava/lang/String;)I"))
    private int readMeta(String s) {
        return "*".equals(s) ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(s);
    }
}
