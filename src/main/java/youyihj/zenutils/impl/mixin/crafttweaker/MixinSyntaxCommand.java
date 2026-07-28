package youyihj.zenutils.impl.mixin.crafttweaker;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.ZenUtils;

/**
 * @author youyihj
 */
@Mixin(targets = "crafttweaker.mc1120.commands.Commands$27", remap = false)
public abstract class MixinSyntaxCommand {
    @Inject(method = "executeCommand", at = @At("TAIL"))
    private void onExecuteCommand(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) {
        ZenUtils.tweaker.finishSyntaxCommand();
    }
}
