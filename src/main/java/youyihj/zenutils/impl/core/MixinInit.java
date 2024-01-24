package youyihj.zenutils.impl.core;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import youyihj.zenutils.api.util.ReflectionInvoked;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class MixinInit implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        List<String> config = Lists.newArrayList("mixins.zenutils.json");
        if (Loader.isModLoaded("simpledimensions")) {
            config.add("mixins.zenutils.simpledimensions.json");
        }
        return config;
    }
}
