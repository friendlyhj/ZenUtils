package youyihj.zenutils.impl.mixin.customscriptentrypoint;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import youyihj.zenutils.impl.zenscript.entrypoint.CustomScriptEntrypoint;

import java.lang.reflect.Method;

/**
 * @author youyihj
 */
@Mixin(value = FMLModContainer.class, remap = false)
public class MixinFMLModContainer {
    @WrapOperation(method = "handleModStateEvent", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Method;invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object zenutils$handleModStateEvent(Method instance, Object o, Object[] objects, Operation<Object> original, @Local(argsOnly = true) FMLEvent event) {
        CustomScriptEntrypoint.runScript(o, event, true);
        original.call(instance, o, objects);
        CustomScriptEntrypoint.runScript(o, event, false);
        // the original method always return null;
        return null;
    }
}
