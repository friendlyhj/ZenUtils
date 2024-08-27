package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.logger.MTLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.runtime.ZenUtilsFileLogger;
import youyihj.zenutils.impl.runtime.ZenUtilsLogger;

import java.nio.file.FileSystems;

/**
 * @author youyihj
 */
@Mixin(value = CrafttweakerImplementationAPI.class, remap = false)
public abstract class MixinCraftTweakerImplementationAPI {
    @Mutable
    @Shadow @Final public static MTLogger logger;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void redirectLogger(CallbackInfo ci) {
        logger = new ZenUtilsLogger();
        logger.addLogger(new ZenUtilsFileLogger(FileSystems.getDefault().getPath("crafttweaker.log")));
    }
}
