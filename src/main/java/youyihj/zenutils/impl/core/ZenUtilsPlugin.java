package youyihj.zenutils.impl.core;

import crafttweaker.CraftTweakerAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@ReflectionInvoked
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1)
public class ZenUtilsPlugin implements IFMLLoadingPlugin {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String[] EARLY_CLASS_LOADING_ERROR_TRIGGERS = new String[]{
            "net.minecraft.item.ItemStack",
            "net.minecraft.block.Block",
            "net.minecraft.entity.item.EntityItem",
            "net.minecraftforge.fml.common.FMLCommonHandler",
            "net.minecraftforge.common.MinecraftForge",
            "net.minecraftforge.fml.common.Loader"
    };

    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<>();
        transformers.add("youyihj.zenutils.impl.config.ClassProvider");
        transformers.add("youyihj.zenutils.impl.core.TConTraitRepresentationTransformer");
        return transformers.toArray(new String[0]);
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (Configuration.enableMixin) {
            try {
                LOGGER.info("Loading MixinZS...");
                ZenMixin.load();
                LOGGER.info("MixinZS loaded.");
            } catch (Exception e) {
                CraftTweakerAPI.logError("Failed to load ZenMixin.", e);
                LOGGER.error("Failed to load ZenMixin.", e);
            }
        }

        try {
            for (String name : EARLY_CLASS_LOADING_ERROR_TRIGGERS) {
                if (InternalUtils.isClassLoadedOnLCL(name)) {
                    CraftTweakerAPI.logError("Mixin scripts shouldn't execute code (i.e. top level statements) related to Minecraft.");
                    LOGGER.fatal("Mixin scripts shouldn't execute code (i.e. top level statements) related to Minecraft.");
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public String getAccessTransformerClass() {
        return "youyihj.zenutils.impl.core.ConfigAccessTransformer";
    }

}
