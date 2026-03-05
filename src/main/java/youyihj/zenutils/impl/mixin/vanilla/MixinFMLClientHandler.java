package youyihj.zenutils.impl.mixin.vanilla;

import com.google.common.collect.BiMap;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import youyihj.zenutils.impl.config.ConfigAnytimeAnytime;

import java.util.ArrayList;

@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandler {
    @Shadow
    private BiMap<ModContainer, IModGuiFactory> guiFactories;

    @Inject(
            method = "addSpecialModEntries",
            at = @At("TAIL"),
            remap = false
    )
    private void addDummyMods(ArrayList<ModContainer> mods, CallbackInfo ci) {
        mods.addAll(ConfigAnytimeAnytime.registeredGuiContainers.values());
    }

    @Inject(
            method = "finishMinecraftLoading",
            at = @At("TAIL"),
            remap = false
    )
    private void addDefaultGuiFactory(CallbackInfo ci){
        ConfigAnytimeAnytime.registeredGuiContainers.values().forEach(container -> this.guiFactories.put(container, DefaultGuiFactory.forMod(container)));
    }
}
