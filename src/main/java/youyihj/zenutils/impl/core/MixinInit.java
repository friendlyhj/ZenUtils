package youyihj.zenutils.impl.core;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;
import zone.rong.mixinbooter.service.ModDiscoverer;

import java.lang.reflect.Method;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class MixinInit implements IMixinConnector {
    private static final String[] EARLY_CLASS_LOADING_ERROR_TRIGGERS = new String[]{
            "net.minecraft.item.ItemStack",
            "net.minecraft.block.Block",
            "net.minecraft.entity.item.EntityItem",
            "net.minecraftforge.fml.common.FMLCommonHandler",
            "net.minecraftforge.common.MinecraftForge"
    };

    @Override
    public void connect() {
        Mixins.addConfiguration("mixins.zenutils.vanilla.json");
        Mixins.addConfiguration("mixins.zenutils.json");
        if (Configuration.enableRandomTickEvent) {
            Mixins.addConfiguration("mixins.zenutils.randomtickevent.json");
        }
        if (ModDiscoverer.isModPresent("simpledimensions")) {
            Mixins.addConfiguration("mixins.zenutils.simpledimensions.json");
        }
        if (Configuration.enableMixin) {
            try {
                ZenMixin.load();
            } catch (Exception e) {
                CraftTweakerAPI.logError("Failed to load ZenMixin.", e);
            }
        }

        try {
            Method classLoader$findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            classLoader$findLoadedClass.setAccessible(true);
            for (String name : EARLY_CLASS_LOADING_ERROR_TRIGGERS) {
                if (classLoader$findLoadedClass.invoke(Launch.classLoader, name) != null) {
                    CraftTweakerAPI.logError("Mixin scripts shouldn't execute code (i.e. top level statements) related to Minecraft.");
                }
            }
        } catch (Exception ignored) {}
    }
}
