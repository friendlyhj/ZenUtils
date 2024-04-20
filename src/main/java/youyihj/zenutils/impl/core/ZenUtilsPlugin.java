package youyihj.zenutils.impl.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import youyihj.zenutils.api.util.ReflectionInvoked;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
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
        return new String[] {"youyihj.zenutils.impl.core.ZenUtilsClassTransformer"};
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
        return Collections.singletonList("mixins.zenutils.vanilla.json");
    }
}
