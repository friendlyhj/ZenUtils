package youyihj.zenutils.impl.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import youyihj.zenutils.api.util.ReflectionInvoked;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
@ReflectionInvoked
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ZenUtilsPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
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

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        configs.add("mixins.zenutils.vanilla.json");
        if (Configuration.enableMixin) {
            configs.add("mixins.zenutils.zenbootstrap.json");
        }
        if (Configuration.enableRandomTickEvent) {
            configs.add("mixins.zenutils.randomtickevent.json");
        }
        return configs;
    }
}
