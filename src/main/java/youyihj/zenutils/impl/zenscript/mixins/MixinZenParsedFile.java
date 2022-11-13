package youyihj.zenutils.impl.zenscript.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.ParsedFunction;
import youyihj.zenutils.impl.zenscript.ExpansionKeyword;
import youyihj.zenutils.impl.zenscript.ExpansionManager;
import youyihj.zenutils.impl.zenscript.ParsedExpansion;

import java.util.Map;

/**
 * @author youyihj
 */
@Mixin(value = ZenParsedFile.class, remap = false)
public class MixinZenParsedFile {
    @Shadow @Final private IEnvironmentGlobal environmentScript;

    @Shadow @Final private Map<String, ParsedFunction> functions;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/ZenTokener;hasNext()Z"))
    private boolean getExpandStatement(ZenTokener instance) {
        boolean hasNext = instance.hasNext();

        if (hasNext && instance.peek().getType() == ExpansionKeyword.ID) {
            ParsedExpansion expansion = ParsedExpansion.parse(instance, environmentScript, ((ZenParsedFile) (Object) this));
            ParsedFunction function = expansion.getFunction();
            if(functions.containsKey(function.getName())) {
                environmentScript.error(function.getPosition(), "function " + function.getName() + " already exists");
            }
            functions.put(function.getName(), function);
            ExpansionManager.register(expansion);
            return instance.next() != null;
        }
        return hasNext;
    }
}
